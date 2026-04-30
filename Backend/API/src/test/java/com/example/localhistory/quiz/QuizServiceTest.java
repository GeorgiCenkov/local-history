package com.example.localhistory.quiz;

import com.example.localhistory.landmark.LandmarkRepository;
import com.example.localhistory.quiz.dto.request.QuizQuestionAnswerRequest;
import com.example.localhistory.quiz.dto.request.QuizSubmissionRequest;
import com.example.localhistory.quiz.dto.response.PublicQuizDTO;
import com.example.localhistory.quiz.dto.response.QuizSubmissionResultDTO;
import com.example.localhistory.user.UserRepository;
import com.example.localhistory.user.UserService;
import com.example.localhistory.user.model.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizCompletionRepository quizCompletionRepository;

    @Mock
    private LandmarkRepository landmarkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private QuizMapper mapper;

    @InjectMocks
    private QuizService service;

    @Test
    void submitQuizAwardsOnePercentOfRequiredPointsPerCorrectAnswer() {
        Student student = studentWithProgress(10L, 200);
        Quiz quiz = quizWithQuestions(
                question(101L, "A"),
                question(102L, "B"),
                question(103L, "C"));
        QuizSubmissionRequest request = submission(
                answer(101L, "A"),
                answer(102L, "B"),
                answer(103L, "wrong"));

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(quizCompletionRepository.existsByStudentIdAndQuizId(10L, 1L)).thenReturn(false);

        QuizSubmissionResultDTO result = service.submitQuiz("student@example.com", 1L, request);

        assertThat(result.getCorrectAnswers()).isEqualTo(2);
        assertThat(result.getAwardedPoints()).isEqualTo(4);
        assertThat(result.isAlreadyCompleted()).isFalse();
        verify(userService).awardPoints(10L, 4);

        ArgumentCaptor<QuizCompletion> completionCaptor = ArgumentCaptor.forClass(QuizCompletion.class);
        verify(quizCompletionRepository).save(completionCaptor.capture());
        assertThat(completionCaptor.getValue().getAwardedPoints()).isEqualTo(4);
    }

    @Test
    void submitQuizDoesNotAwardPointsWhenNoAnswersAreCorrect() {
        Student student = studentWithProgress(10L, 200);
        Quiz quiz = quizWithQuestions(question(101L, "A"));
        QuizSubmissionRequest request = submission(answer(101L, "wrong"));

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(quizCompletionRepository.existsByStudentIdAndQuizId(10L, 1L)).thenReturn(false);

        QuizSubmissionResultDTO result = service.submitQuiz("student@example.com", 1L, request);

        assertThat(result.getCorrectAnswers()).isZero();
        assertThat(result.getAwardedPoints()).isZero();
        verify(userService, never()).awardPoints(anyLong(), anyInt());
        verify(quizCompletionRepository).save(any(QuizCompletion.class));
    }

    @Test
    void submitQuizAwardsAtLeastOnePointForOneCorrectQuestion() {
        Student student = studentWithProgress(10L, 100);
        Quiz quiz = quizWithQuestions(question(101L, "A"));
        QuizSubmissionRequest request = submission(answer(101L, "A"));

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(quizCompletionRepository.existsByStudentIdAndQuizId(10L, 1L)).thenReturn(false);

        QuizSubmissionResultDTO result = service.submitQuiz("student@example.com", 1L, request);

        assertThat(result.getCorrectAnswers()).isEqualTo(1);
        assertThat(result.getAwardedPoints()).isEqualTo(1);
        verify(userService).awardPoints(10L, 1);
    }

    @Test
    void submitQuizFallsBackToDefaultRequiredPointsWhenStudentProgressIsMissing() {
        Student student = studentWithProgress(10L, null);
        Quiz quiz = quizWithQuestions(question(101L, "A"));
        QuizSubmissionRequest request = submission(answer(101L, "A"));

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(quizCompletionRepository.existsByStudentIdAndQuizId(10L, 1L)).thenReturn(false);

        QuizSubmissionResultDTO result = service.submitQuiz("student@example.com", 1L, request);

        assertThat(result.getAwardedPoints()).isEqualTo(1);
        verify(userService).awardPoints(10L, 1);
    }

    @Test
    void submitQuizGradesRetakeButDoesNotAwardPointsAgain() {
        Student student = studentWithProgress(10L, 100);
        Quiz quiz = quizWithQuestions(question(101L, "A"));
        QuizSubmissionRequest request = submission(answer(101L, "A"));

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(quizCompletionRepository.existsByStudentIdAndQuizId(10L, 1L)).thenReturn(true);

        QuizSubmissionResultDTO result = service.submitQuiz("student@example.com", 1L, request);

        assertThat(result.getCorrectAnswers()).isEqualTo(1);
        assertThat(result.getAwardedPoints()).isZero();
        assertThat(result.isAlreadyCompleted()).isTrue();
        verify(userService, never()).awardPoints(anyLong(), anyInt());
        verify(quizCompletionRepository, never()).save(any(QuizCompletion.class));
    }

    @Test
    void getQuizByIdMarksQuizAlreadyCompletedForStudent() {
        Student student = studentWithProgress(10L, 100);
        Quiz quiz = quizWithQuestions(question(101L, "A"));
        PublicQuizDTO publicQuiz = new PublicQuizDTO();

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(mapper.toPublicDTO(quiz)).thenReturn(publicQuiz);
        when(quizCompletionRepository.existsByStudentIdAndQuizId(10L, 1L)).thenReturn(true);

        PublicQuizDTO result = service.getQuizById("student@example.com", 1L);

        assertThat(result.isAlreadyCompleted()).isTrue();
    }

    private Student studentWithProgress(Long id, Integer pointsRequired) {
        Student student = new Student();
        student.setId(id);
        student.setPointsRequired(pointsRequired);
        return student;
    }

    private Quiz quizWithQuestions(QuizQuestion... questions) {
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        quiz.setQuestions(List.of(questions));
        return quiz;
    }

    private QuizQuestion question(Long id, String correctAnswer) {
        QuizQuestion question = new QuizQuestion();
        question.setId(id);
        question.setQuestion("Question " + id);
        question.setOptions(List.of(correctAnswer, "wrong"));
        question.setCorrectAnswer(correctAnswer);
        return question;
    }

    private QuizSubmissionRequest submission(QuizQuestionAnswerRequest... answers) {
        QuizSubmissionRequest request = new QuizSubmissionRequest();
        request.setAnswers(List.of(answers));
        return request;
    }

    private QuizQuestionAnswerRequest answer(Long questionId, String submittedAnswer) {
        QuizQuestionAnswerRequest answer = new QuizQuestionAnswerRequest();
        answer.setQuestionId(questionId);
        answer.setAnswer(submittedAnswer);
        return answer;
    }
}
