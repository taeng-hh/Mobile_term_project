package com.example.term_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;

public class MainFragment extends Fragment {

    // 기본 캐릭터 이미지
    private ImageView characterImage;

    // 추가 꾸미기 레이어 이미지
    private ImageView clothesImage;
    private ImageView faceImage;
    private ImageView hatImage;

    // Fragment끼리 공유할 ViewModel
    private CharacterViewModel viewModel;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // 메인 캐릭터 레이어 연결
        characterImage = view.findViewById(R.id.characterImage);
        clothesImage = view.findViewById(R.id.clothes_image);
        faceImage = view.findViewById(R.id.face_image);
        hatImage = view.findViewById(R.id.hat_image);

        // Activity 범위의 ViewModel 공유
        // RightFragment에서 바꾼 값을 MainFragment도 같이 보게 됨
        viewModel = new ViewModelProvider(requireActivity()).get(CharacterViewModel.class);

        // 기본 캐릭터 몸체 관찰
        // 나중에 캐릭터 형태까지 바꾸고 싶으면 이 부분이 필요함
        // 현재 ViewModel에 character 항목이 없다면 이 줄은 잠시 빼도 됨
        viewModel.getCharacter().observe(getViewLifecycleOwner(), resId -> {
            characterImage.setImageResource(resId);
        });

        // 얼굴 상태 관찰
        viewModel.getFace().observe(getViewLifecycleOwner(), resId -> {
            if (resId != 0) {
                faceImage.setImageResource(resId);
            } else {
                faceImage.setImageDrawable(null);
            }
        });

        // 모자 상태 관찰
        viewModel.getHat().observe(getViewLifecycleOwner(), resId -> {
            if (resId != 0) {
                hatImage.setImageResource(resId);
            } else {
                hatImage.setImageDrawable(null);
            }
        });

        // 옷 상태 관찰
        viewModel.getClothes().observe(getViewLifecycleOwner(), resId -> {
            if (resId != 0) {
                clothesImage.setImageResource(resId);
            } else {
                clothesImage.setImageDrawable(null);
            }
        });

        // 설정 UI 연결
        ImageButton btnSettings = view.findViewById(R.id.btnSettings);
        View dimView = view.findViewById(R.id.dimView);
        LinearLayout settingsPopup = view.findViewById(R.id.settingsPopup);

        Button btnSound = view.findViewById(R.id.btnSound);
        Button btnHelp = view.findViewById(R.id.btnHelp);
        Button btnClosePopup = view.findViewById(R.id.btnClosePopup);

        // 설정 버튼 클릭 시 팝업 열기
        btnSettings.setOnClickListener(v -> {
            dimView.setVisibility(View.VISIBLE);
            settingsPopup.setVisibility(View.VISIBLE);
        });

        // 닫기 버튼 클릭 시 팝업 닫기
        btnClosePopup.setOnClickListener(v -> {
            dimView.setVisibility(View.GONE);
            settingsPopup.setVisibility(View.GONE);
        });

        // 팝업 바깥 클릭 시 닫기
        dimView.setOnClickListener(v -> {
            dimView.setVisibility(View.GONE);
            settingsPopup.setVisibility(View.GONE);
        });

        // 기능 버튼 예시
        btnSound.setOnClickListener(v ->
                Toast.makeText(getActivity(), "소리 설정", Toast.LENGTH_SHORT).show());

        btnHelp.setOnClickListener(v ->
                Toast.makeText(getActivity(), "도움말", Toast.LENGTH_SHORT).show());

        return view;
    }
}