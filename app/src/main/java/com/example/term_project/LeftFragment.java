package com.example.term_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LeftFragment extends Fragment {

    private SharedPreferences prefs;

    private LinearLayout cardQuiz1;
    private LinearLayout cardQuiz2;
    private LinearLayout cardQuiz3;
    private LinearLayout cardLocked1;
    private LinearLayout cardLocked2;

    public LeftFragment() {
        // 기본 생성자
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_left, container, false);

        prefs = requireContext().getSharedPreferences("quiz_progress", Context.MODE_PRIVATE);

        // 카드 연결
        cardQuiz1 = view.findViewById(R.id.cardQuiz1);
        cardQuiz2 = view.findViewById(R.id.cardQuiz2);
        cardQuiz3 = view.findViewById(R.id.cardQuiz3);
        cardLocked1 = view.findViewById(R.id.cardLocked1);
        cardLocked2 = view.findViewById(R.id.cardLocked2);

        refreshCards();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCards();
    }

    /**
     * 전체 카드 상태를 갱신한다.
     */
    private void refreshCards() {
        setupCard(cardQuiz1, 1);
        setupCard(cardQuiz2, 2);
        setupCard(cardQuiz3, 3);
        setupCard(cardLocked1, 4);
        setupCard(cardLocked2, 5);
    }

    /**
     * 각 카드의 입장 가능 여부를 설정한다.
     */
    private void setupCard(LinearLayout card, int stageId) {
        if (card == null) {
            return;
        }

        boolean canPlay = canPlayStage(stageId);

        if (canPlay) {
            card.setAlpha(1.0f);
            card.setOnClickListener(v -> moveToQuizPlay(stageId));
        } else {
            card.setAlpha(0.5f);
            card.setOnClickListener(v ->
                    Toast.makeText(getContext(),
                            getLockedMessage(stageId),
                            Toast.LENGTH_SHORT).show()
            );
        }
    }

    /**
     * 해당 스테이지를 플레이할 수 있는지 확인한다.
     * 1단계는 항상 플레이 가능하다.
     * 2단계 이상은 before_clear 값이 1이어야 입장 가능하다.
     */
    private boolean canPlayStage(int stageId) {
        if (stageId == 1) {
            return true;
        }

        return prefs.getInt("stage_" + stageId + "_before_clear", 0) == 1;
    }

    /**
     * 잠긴 카드 클릭 시 안내 메시지
     */
    private String getLockedMessage(int stageId) {
        if (stageId == 1) {
            return "플레이 가능합니다.";
        }

        return (stageId - 1) + "단계를 먼저 클리어해야 합니다.";
    }

    /**
     * 퀴즈 화면으로 이동한다.
     */
    private void moveToQuizPlay(int stageId) {
        Bundle bundle = new Bundle();
        bundle.putInt("subject_id", stageId);

        QuizPlayFragment fragment = new QuizPlayFragment();
        fragment.setArguments(bundle);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openFragment(fragment);
        }
    }
}