package com.example.localhistory.quiz;

import com.example.localhistory.landmark.LandmarkRepository;
import com.example.localhistory.quiz.dto.request.QuizQuestionAnswerRequest;
import com.example.localhistory.quiz.dto.request.QuizSubmissionRequest;
import com.example.localhistory.quiz.dto.response.QuizSubmissionResultDTO;
import com.example.localhistory.user.UserRepository;
import com.example.localhistory.user.UserService;
import com.example.localhistory.user.model.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

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

        QuizSubmissionResultDTO result = service.submitQuiz("student@example.com", 1L, request);

        assertThat(result.getCorrectAnswers()).isEqualTo(2);
        assertThat(result.getAwardedPoints()).isEqualTo(4);
        verify(userService).awardPoints(10L, 4);
    }

    @Test
    void submitQuizDoesNotAwardPointsWhenNoAnswersAreCorrect() {
        Student student = studentWithProgress(10L, 200);
        Quiz quiz = quizWithQuestions(question(101L, "A"));
        QuizSubmissionRequest request = submission(answer(101L, "wrong"));

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        QuizSubmissionResultDTO result = service.submitQuiz("student@example.com", 1L, request);

        assertThat(result.getCorrectAnswers()).isZero();
        assertThat(result.getAwardedPoints()).isZero();
        verify(userService, never()).awardPoints(anyLong(), anyInt());
    }

    private Student studentWithProgress(Long id, int pointsRequired) {
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
