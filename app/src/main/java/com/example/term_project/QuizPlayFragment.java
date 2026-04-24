package com.example.term_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class QuizPlayFragment extends Fragment {

    private TextView tvQuestion;
    private RadioGroup radioGroup;
    private RadioButton option1, option2, option3, option4;
    private Button btnSubmit;

    private int totalSolvedCount = 0;
    private int correctCount = 0;
    private int earnedGold = 0;

    private com.airbnb.lottie.LottieAnimationView lottieEffect;
    private View layoutResult;
    private TextView tvResultStatus;

    private QuizRepository repository;
    private QuizQuestion currentQuestion;

    private int currentSubjectId;
    private int currentQuestionId = 1;

    public QuizPlayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quiz_play, container, false);

        tvQuestion = view.findViewById(R.id.tvQuestion);
        radioGroup = view.findViewById(R.id.radioGroupOptions);
        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);
        option4 = view.findViewById(R.id.option4);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        applyPressAnimation(btnSubmit);
        lottieEffect = view.findViewById(R.id.lottieEffect);
        layoutResult = view.findViewById(R.id.layoutResult);
        tvResultStatus = view.findViewById(R.id.tvResultStatus);

        repository = new QuizRepository();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> updateOptionBackgrounds());

        if (getArguments() != null) {
            currentSubjectId = getArguments().getInt("subject_id");
            loadQuestion(currentSubjectId, currentQuestionId);
        }

        btnSubmit.setOnClickListener(v -> checkAnswer());

        return view;
    }

    private void loadQuestion(int subjectId, int questionId) {
        repository.getQuizQuestionFromFirestore(subjectId, questionId, new QuizRepository.OnQuestionFetchedListener() {

            @Override
            public void onSuccess(QuizQuestion question) {
                if (!isAdded()) {
                    return;
                }

                currentQuestion = question;
                bindQuestion(question);

                radioGroup.clearCheck();
                updateOptionBackgrounds();
            }

            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) {
                    return;
                }

                if (currentQuestionId == 1) {
                    tvQuestion.setText("문제를 찾을 수 없습니다.");
                    btnSubmit.setEnabled(false);
                    return;
                }

                completeStageIfAllCorrect();
                showFinalResult();
            }
        });
    }

    private void bindQuestion(QuizQuestion question) {
        if (question == null) {
            return;
        }

        tvQuestion.setText(question.getQuestion());
        String[] options = question.getOptions();

        option1.setVisibility(View.GONE);
        option2.setVisibility(View.GONE);
        option3.setVisibility(View.GONE);
        option4.setVisibility(View.GONE);

        if (options != null) {
            if (options.length > 0) {
                option1.setText(options[0]);
                option1.setVisibility(View.VISIBLE);
            }
            if (options.length > 1) {
                option2.setText(options[1]);
                option2.setVisibility(View.VISIBLE);
            }
            if (options.length > 2) {
                option3.setText(options[2]);
                option3.setVisibility(View.VISIBLE);
            }
            if (options.length > 3) {
                option4.setText(options[3]);
                option4.setVisibility(View.VISIBLE);
            }
        }
    }

    private void checkAnswer() {
        if (currentQuestion == null) {
            return;
        }

        int checkedId = radioGroup.getCheckedRadioButtonId();

        if (checkedId == -1) {
            Toast.makeText(getContext(), "정답을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedIndex = -1;

        if (checkedId == R.id.option1) {
            selectedIndex = 0;
        } else if (checkedId == R.id.option2) {
            selectedIndex = 1;
        } else if (checkedId == R.id.option3) {
            selectedIndex = 2;
        } else if (checkedId == R.id.option4) {
            selectedIndex = 3;
        }

        if (selectedIndex == currentQuestion.getCorrectAnswerIndex()) {
            handleResult(true);
        } else {
            handleResult(false);
        }
    }

    private void handleResult(boolean isCorrect) {
        btnSubmit.setEnabled(false);
        totalSolvedCount++;

        if (isCorrect) {
            correctCount++;
            int goldToAdd = calculateGold();
            earnedGold += goldToAdd;

            playEffect(R.raw.success, "정답입니다!\n+" + goldToAdd + "G", true);
        } else {
            playEffect(R.raw.fail, "오답입니다!", true);
        }
    }

    private void playEffect(int rawResId, String statusText, boolean moveNextQuestion) {
        layoutResult.setVisibility(View.VISIBLE);
        tvResultStatus.setText(statusText);

        lottieEffect.setAnimation(rawResId);
        lottieEffect.playAnimation();

        lottieEffect.removeAllAnimatorListeners();
        lottieEffect.addAnimatorListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {
            }

            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                lottieEffect.removeAllAnimatorListeners();

                layoutResult.postDelayed(() -> {
                    layoutResult.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);

                    if (moveNextQuestion) {
                        currentQuestionId++;
                        loadQuestion(currentSubjectId, currentQuestionId);
                    }
                }, 500);
            }

            @Override
            public void onAnimationCancel(android.animation.Animator animation) {
            }

            @Override
            public void onAnimationRepeat(android.animation.Animator animation) {
            }
        });
    }

    private int calculateGold() {
        if (currentQuestion == null) {
            return 0;
        }

        String level = currentQuestion.getDifficultyLevel();

        if ("hard".equals(level)) {
            return 30;
        }

        if ("normal".equals(level)) {
            return 20;
        }

        return 10;
    }

    private void completeStageIfAllCorrect() {
        if (totalSolvedCount == 0) {
            return;
        }

        boolean isPerfectClear = (correctCount == totalSolvedCount);

        if (!isPerfectClear) {
            return;
        }

        SharedPreferences prefs = requireContext()
                .getSharedPreferences("quiz_progress", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("stage_" + currentSubjectId + "_clear", 1);
        editor.putInt("stage_" + (currentSubjectId + 1) + "_before_clear", 1);

        editor.apply();
    }

    private void showFinalResult() {
        if (getContext() == null) {
            return;
        }

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).addGold(earnedGold);
        }

        boolean isPerfectClear = (totalSolvedCount > 0 && correctCount == totalSolvedCount);

        String clearMessage;
        if (isPerfectClear) {
            clearMessage = "단계 클리어: 성공\n다음 단계가 해금되었습니다.\n\n";
        } else {
            clearMessage = "단계 클리어: 실패\n모든 문제를 맞혀야 다음 단계가 해금됩니다.\n\n";
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("퀴즈 결과")
                .setMessage(
                        clearMessage +
                                "총 푼 문제 수: " + totalSolvedCount + "문제\n" +
                                "틀린 문제 수: " + (totalSolvedCount - correctCount) + "문제\n" +
                                "맞춘 문제 수: " + correctCount + "문제\n" +
                                "획득 골드: " + earnedGold + "G"
                )
                .setCancelable(false)
                .setPositiveButton("확인", (dialog, which) -> closeFragment())
                .show();
    }

    private void closeFragment() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).closeCurrentFragment();
        }
    }

    private void updateOptionBackgrounds() {
        option1.setBackgroundResource(option1.isChecked()
                ? R.drawable.bg_quiz_option_selected
                : R.drawable.bg_message_box);

        option2.setBackgroundResource(option2.isChecked()
                ? R.drawable.bg_quiz_option_selected
                : R.drawable.bg_message_box);

        option3.setBackgroundResource(option3.isChecked()
                ? R.drawable.bg_quiz_option_selected
                : R.drawable.bg_message_box);

        option4.setBackgroundResource(option4.isChecked()
                ? R.drawable.bg_quiz_option_selected
                : R.drawable.bg_message_box);
    }
    private void applyPressAnimation(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    v.animate()
                            .scaleX(0.96f)
                            .scaleY(0.96f)
                            .setDuration(80)
                            .start();
                    break;

                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(80)
                            .start();
                    break;
            }
            return false;
        });
    }
}
