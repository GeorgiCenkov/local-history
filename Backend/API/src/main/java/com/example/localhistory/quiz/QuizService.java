package com.example.localhistory.quiz;

import com.example.localhistory.exception.ResourceConflictException;
import com.example.localhistory.landmark.LandmarkRepository;
import com.example.localhistory.landmark.model.Landmark;
import com.example.localhistory.quiz.dto.request.QuizQuestionAnswerRequest;
import com.example.localhistory.quiz.dto.request.QuizQuestionRequest;
import com.example.localhistory.quiz.dto.request.QuizRequest;
import com.example.localhistory.quiz.dto.request.QuizSubmissionRequest;
import com.example.localhistory.quiz.dto.response.PublicQuizDTO;
import com.example.localhistory.quiz.dto.response.QuizDTO;
import com.example.localhistory.quiz.dto.response.QuizQuestionResultDTO;
import com.example.localhistory.quiz.dto.response.QuizSubmissionResultDTO;
import com.example.localhistory.user.UserRepository;
import com.example.localhistory.user.UserService;
import com.example.localhistory.user.model.Student;
import com.example.localhistory.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final LandmarkRepository landmarkRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final QuizMapper mapper;

    /** Returns all quizzes without correct answers for regular client reads. */
    @Transactional(readOnly = true)
    public List<PublicQuizDTO> getAllQuizzes() {
        return quizRepository.findAll()
                .stream()
                .map(mapper::toPublicDTO)
                .toList();
    }

    /** Returns every quiz owned by the authenticated teacher. */
    @Transactional(readOnly = true)
    public List<QuizDTO> getTeacherQuizzes(String teacherEmail) {
        return quizRepository.findByLandmarkOwnerEmail(teacherEmail)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    /** Fetches a public quiz by ID, omitting the correct answers. */
    @Transactional(readOnly = true)
    public PublicQuizDTO getQuizById(Long id) {
        return mapper.toPublicDTO(findQuizOrThrow(id));
    }

    /** Fetches the quiz attached to a landmark, omitting the correct answers. */
    @Transactional(readOnly = true)
    public PublicQuizDTO getQuizByLandmarkId(Long landmarkId) {
        return mapper.toPublicDTO(quizRepository.findByLandmarkId(landmarkId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found for landmark id: " + landmarkId)));
    }

    /** Fetches a teacher-owned quiz with correct answers for management screens. */
    @Transactional(readOnly = true)
    public QuizDTO getTeacherQuizById(String teacherEmail, Long id) {
        return mapper.toDTO(findQuizForTeacherOrThrow(teacherEmail, id));
    }

    /** Creates one quiz for a teacher-owned landmark. */
    @Transactional
    public QuizDTO createQuiz(String teacherEmail, QuizRequest request) {
        Landmark landmark = findLandmarkForTeacherOrThrow(teacherEmail, request.getLandmarkId());

        if (quizRepository.existsByLandmarkId(request.getLandmarkId())) {
            throw new ResourceConflictException("Landmark already has a quiz");
        }

        Quiz quiz = new Quiz();
        quiz.setLandmark(landmark);
        applyRequest(quiz, request);

        return mapper.toDTO(quizRepository.save(quiz));
    }

    /** Fully replaces the quiz title, landmark, and questions. */
    @Transactional
    public QuizDTO updateQuiz(String teacherEmail, Long id, QuizRequest request) {
        Quiz quiz = findQuizForTeacherOrThrow(teacherEmail, id);
        Landmark landmark = findLandmarkForTeacherOrThrow(teacherEmail, request.getLandmarkId());

        quizRepository.findByLandmarkId(request.getLandmarkId())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResourceConflictException("Landmark already has a quiz");
                });

        quiz.setLandmark(landmark);
        applyRequest(quiz, request);

        return mapper.toDTO(quizRepository.save(quiz));
    }

    /** Permanently removes a teacher-owned quiz. */
    @Transactional
    public void deleteQuiz(String teacherEmail, Long id) {
        quizRepository.delete(findQuizForTeacherOrThrow(teacherEmail, id));
    }

    /**
     * Grades a quiz submission in-memory.
     * No attempt entity exists yet, so this returns the result without saving it.
     */
    @Transactional
    public QuizSubmissionResultDTO submitQuiz(String studentEmail, Long id, QuizSubmissionRequest request) {
        Student student = findStudentOrThrow(studentEmail);
        Quiz quiz = findQuizOrThrow(id);
        Map<Long, String> answersByQuestionId = mapAnswersByQuestionId(request);
        validateAnswersBelongToQuiz(quiz, answersByQuestionId.keySet());

        List<QuizQuestionResultDTO> results = quiz.getQuestions()
                .stream()
                .map(question -> gradeQuestion(question, answersByQuestionId.get(question.getId())))
                .toList();
        int correctAnswers = (int) results.stream().filter(QuizQuestionResultDTO::isCorrect).count();
        int awardedPoints = calculateQuizRewardPoints(student, correctAnswers);

        if (awardedPoints > 0) {
            userService.awardPoints(student.getId(), awardedPoints);
        }

        QuizSubmissionResultDTO result = new QuizSubmissionResultDTO();
        result.setQuizId(quiz.getId());
        result.setTotalQuestions(quiz.getQuestions().size());
        result.setAnsweredQuestions(answersByQuestionId.size());
        result.setCorrectAnswers(correctAnswers);
        result.setAwardedPoints(awardedPoints);
        result.setResults(results);
        return result;
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /** Copies all mutable request fields onto a quiz and rebuilds its questions. */
    private void applyRequest(Quiz quiz, QuizRequest request) {
        quiz.setTitle(request.getTitle());

        List<QuizQuestion> questions = request.getQuestions()
                .stream()
                .map(questionRequest -> toQuestion(quiz, questionRequest))
                .toList();

        if (quiz.getQuestions() == null) {
            quiz.setQuestions(new ArrayList<>());
        } else {
            quiz.getQuestions().clear();
        }

        quiz.getQuestions().addAll(questions);
    }

    /** Builds a question entity and links it back to its owning quiz. */
    private QuizQuestion toQuestion(Quiz quiz, QuizQuestionRequest request) {
        validateCorrectAnswerIsOption(request);

        QuizQuestion question = new QuizQuestion();
        question.setQuiz(quiz);
        question.setQuestion(request.getQuestion());
        question.setOptions(new ArrayList<>(request.getOptions()));
        question.setCorrectAnswer(request.getCorrectAnswer());
        return question;
    }

    /** Prevents impossible quizzes where the correct answer is not selectable. */
    private void validateCorrectAnswerIsOption(QuizQuestionRequest request) {
        if (!request.getOptions().contains(request.getCorrectAnswer())) {
            throw new IllegalArgumentException("Correct answer must match one of the answer options");
        }
    }

    /** Converts submitted answers to a lookup table and rejects duplicate answers. */
    private Map<Long, String> mapAnswersByQuestionId(QuizSubmissionRequest request) {
        Map<Long, String> answersByQuestionId = new HashMap<>();

        for (QuizQuestionAnswerRequest answer : request.getAnswers()) {
            String previous = answersByQuestionId.put(answer.getQuestionId(), answer.getAnswer());

            if (previous != null) {
                throw new IllegalArgumentException("Each question can only be answered once");
            }
        }

        return answersByQuestionId;
    }

    /** Rejects answers for questions that are not part of the requested quiz. */
    private void validateAnswersBelongToQuiz(Quiz quiz, Set<Long> answeredQuestionIds) {
        List<Long> quizQuestionIds = quiz.getQuestions()
                .stream()
                .map(QuizQuestion::getId)
                .toList();

        boolean containsUnknownQuestion = answeredQuestionIds.stream()
                .anyMatch(questionId -> !quizQuestionIds.contains(questionId));

        if (containsUnknownQuestion) {
            throw new IllegalArgumentException("Submitted answers must belong to the quiz");
        }
    }

    /** Grades one question; missing answers are treated as incorrect. */
    private QuizQuestionResultDTO gradeQuestion(QuizQuestion question, String submittedAnswer) {
        QuizQuestionResultDTO result = new QuizQuestionResultDTO();
        result.setQuestionId(question.getId());
        result.setSubmittedAnswer(submittedAnswer);
        result.setCorrectAnswer(question.getCorrectAnswer());
        result.setCorrect(question.getCorrectAnswer().equals(submittedAnswer));
        return result;
    }

    /** Calculates one whole-point reward worth about 1% of the current level requirement. */
    private int calculateQuizRewardPoints(Student student, int correctAnswers) {
        if (correctAnswers == 0) {
            return 0;
        }

        int pointsPerCorrectAnswer = Math.max(1, Math.round(student.getPointsRequired() * 0.01f));
        return pointsPerCorrectAnswer * correctAnswers;
    }

    /** Finds the submitting student; role is still enforced at the controller layer. */
    private Student findStudentOrThrow(String studentEmail) {
        User user = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + studentEmail));

        if (user instanceof Student student) {
            return student;
        }

        throw new IllegalArgumentException("Only students can submit quizzes");
    }

    /** Centralizes the public "find or 404" path. */
    private Quiz findQuizOrThrow(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + id));
    }

    /** Centralizes teacher ownership checks for quiz management. */
    private Quiz findQuizForTeacherOrThrow(String teacherEmail, Long id) {
        return quizRepository.findByIdAndLandmarkOwnerEmail(id, teacherEmail)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + id));
    }

    /** Reuses landmark ownership as the authorization boundary for quizzes. */
    private Landmark findLandmarkForTeacherOrThrow(String teacherEmail, Long landmarkId) {
        return landmarkRepository.findByIdAndOwnerEmail(landmarkId, teacherEmail)
                .orElseThrow(() -> new EntityNotFoundException("Landmark not found with id: " + landmarkId));
    }
}
