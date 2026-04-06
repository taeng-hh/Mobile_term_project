package com.example.term_project;

import android.content.ClipData;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // ViewModel 사용을 위한 import

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RightFragment extends Fragment {

    // 아이템 목록이 들어갈 GridLayout
    private GridLayout imageGrid;

    // 전체 Fragment에서 공유할 ViewModel
    private CharacterViewModel viewModel;

    // 캐릭터 레이어 이미지들 (현재 화면 표시용)
    private ImageView faceImage, hatImage, clothesImage;

    // 표정 이미지 리스트
    private List<Integer> faceList = Arrays.asList(
            R.drawable.face_happy, R.drawable.face_sad, R.drawable.face_hungry
    );

    // 모자 이미지 리스트
    private List<Integer> hatList = Arrays.asList(
            R.drawable.hat_halloween, R.drawable.hat_hiphop, R.drawable.hat_onepiece,
            R.drawable.hat_crown, R.drawable.hat_rabbit
    );

    // 옷 이미지 리스트
    private List<Integer> clothesList = Arrays.asList(
            R.drawable.clothes_halloween, R.drawable.clothes_hiphop, R.drawable.clothes_onepiece,
            R.drawable.clothes_pokemon, R.drawable.clothes_santa
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_right, container, false);

        // ViewModel 연결 (Activity 범위 공유)
        // 다른 Fragment(MainFragment 등)와 같은 데이터를 사용하기 위함
        viewModel = new ViewModelProvider(requireActivity()).get(CharacterViewModel.class);

        // 화면 요소 연결
        imageGrid = view.findViewById(R.id.image_grid);
        faceImage = view.findViewById(R.id.face_image);
        hatImage = view.findViewById(R.id.hat_image);
        clothesImage = view.findViewById(R.id.clothes_image);

        FrameLayout topArea = view.findViewById(R.id.top_area);
        Button btnReset = view.findViewById(R.id.btn_reset);

        // 드래그 후 캐릭터 영역에 드롭했을 때 처리
        topArea.setOnDragListener((v, event) -> {
            if (event.getAction() == DragEvent.ACTION_DROP) {

                // 드래그된 데이터 가져오기
                ClipData.Item item = event.getClipData().getItemAt(0);

                // 문자열 → int로 변환 (이미지 리소스 id)
                int resId = Integer.parseInt(item.getText().toString());

                // ViewModel에 값 반영
                updateCharacter(resId);
            }
            return true;
        });

        // 초기화 버튼
        btnReset.setOnClickListener(v -> resetCharacter());

        // 카테고리 버튼
        view.findViewById(R.id.btn_all).setOnClickListener(v -> showImages(getAllImages()));
        view.findViewById(R.id.btn_face).setOnClickListener(v -> showImages(faceList));
        view.findViewById(R.id.btn_hat).setOnClickListener(v -> showImages(hatList));
        view.findViewById(R.id.btn_top).setOnClickListener(v -> showImages(clothesList));

        // 초기 상태 (전체 보기)
        showImages(getAllImages());

        // ===== ViewModel 상태 관찰 =====

        // 얼굴 변경 감지
        viewModel.getFace().observe(getViewLifecycleOwner(), resId -> {
            faceImage.setImageResource(resId);
        });

        // 모자 변경 감지
        viewModel.getHat().observe(getViewLifecycleOwner(), resId -> {

            // 0이면 착용 안함 상태
            if (resId != 0) {
                hatImage.setImageResource(resId);
            } else {
                hatImage.setImageDrawable(null);
            }
        });

        // 옷 변경 감지
        viewModel.getClothes().observe(getViewLifecycleOwner(), resId -> {

            if (resId != 0) {
                clothesImage.setImageResource(resId);
            } else {
                clothesImage.setImageDrawable(null);
            }
        });

        return view;
    }

    // 전체 이미지 리스트 생성
    private List<Integer> getAllImages() {
        List<Integer> all = new ArrayList<>();
        all.addAll(faceList);
        all.addAll(hatList);
        all.addAll(clothesList);
        return all;
    }

    // GridLayout에 이미지들을 동적으로 추가
    private void showImages(List<Integer> imageList) {

        if (imageGrid == null) return;

        imageGrid.removeAllViews();

        for (int resId : imageList) {

            ImageView imageView = new ImageView(getContext());

            imageView.setImageResource(resId);

            // 태그에 리소스 ID 저장 (드래그 시 사용)
            imageView.setTag(resId);

            // 길게 눌렀을 때 드래그 시작
            imageView.setOnLongClickListener(v -> {

                // ViewPager 등 부모가 터치 이벤트 가로채지 못하게 함
                v.getParent().requestDisallowInterceptTouchEvent(true);

                ClipData data = ClipData.newPlainText("resId", v.getTag().toString());

                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);

                v.startDragAndDrop(data, shadowBuilder, null, 0);

                return true;
            });

            // GridLayout 배치 설정
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();

            params.width = 0;
            params.height = 350;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(10, 10, 10, 10);

            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            imageGrid.addView(imageView);
        }
    }

    // 드롭된 아이템을 ViewModel에 반영
    private void updateCharacter(int resId) {

        // 어떤 종류인지 판단해서 ViewModel에 저장

        if (faceList.contains(resId)) {
            viewModel.setFace(resId);

        } else if (hatList.contains(resId)) {
            viewModel.setHat(resId);

        } else if (clothesList.contains(resId)) {
            viewModel.setClothes(resId);
        }
    }

    // 캐릭터 초기화
    private void resetCharacter() {
        viewModel.reset();
    }
}