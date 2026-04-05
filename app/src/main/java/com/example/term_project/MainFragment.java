package com.example.term_project;

import android.os.Bundle;
import java.util.Random;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;

public class MainFragment extends Fragment {

    private ImageView characterImage;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // 캐릭터 랜덤 설정
        characterImage = view.findViewById(R.id.characterImage);

        int[] characters = {
                R.drawable.sample1,
                R.drawable.sample2,
                R.drawable.sample3
        };

        Random random = new Random();
        int randomIndex = random.nextInt(characters.length);
        characterImage.setImageResource(characters[randomIndex]);

        // 설정 UI 연결
        ImageButton btnSettings = view.findViewById(R.id.btnSettings);
        View dimView = view.findViewById(R.id.dimView);
        LinearLayout settingsPopup = view.findViewById(R.id.settingsPopup);

        Button btnSound = view.findViewById(R.id.btnSound);
        Button btnHelp = view.findViewById(R.id.btnHelp);
        Button btnClosePopup = view.findViewById(R.id.btnClosePopup);
        Button logoutBtn = view.findViewById(R.id.logoutBtn);

        // 설정 버튼 클릭
        btnSettings.setOnClickListener(v -> {
            dimView.setVisibility(View.VISIBLE);
            settingsPopup.setVisibility(View.VISIBLE);
        });



        // 닫기 버튼
        btnClosePopup.setOnClickListener(v -> {
            dimView.setVisibility(View.GONE);
            settingsPopup.setVisibility(View.GONE);
        });

        // 바깥 클릭 시 닫힘
        dimView.setOnClickListener(v -> {
            dimView.setVisibility(View.GONE);
            settingsPopup.setVisibility(View.GONE);
        });

        // 기능 버튼 (예시)
        btnSound.setOnClickListener(v ->
                Toast.makeText(getActivity(), "소리 설정", Toast.LENGTH_SHORT).show());

        btnHelp.setOnClickListener(v ->
                Toast.makeText(getActivity(), "도움말", Toast.LENGTH_SHORT).show());

        // 로그아웃 버튼(팝업으로 한 번 더 확인 후 로그아웃)
        logoutBtn.setOnClickListener(v -> {

            new AlertDialog.Builder(requireContext())
                    .setTitle("로그아웃")
                    .setMessage("정말 로그아웃 하시겠습니까?")

                    .setPositiveButton("예", (dialog, which) -> {

                        // 자동으로 팝업 닫고 로그아웃
                        dimView.setVisibility(View.GONE);
                        settingsPopup.setVisibility(View.GONE);

                        SharedPreferences pref = requireActivity()
                                .getSharedPreferences("user", getContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();

                        editor.putBoolean("isLogin", false);
                        editor.apply();

                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    })

                    .setNegativeButton("아니오", null)

                    .show();
        });

        return view;
    }
}