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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RightFragment extends Fragment {

    private GridLayout imageGrid;
    private ImageView faceImage, hatImage, clothesImage;

    // 추가하신 이미지 리스트들
    private List<Integer> faceList = Arrays.asList(
            R.drawable.face_happy, R.drawable.face_sad, R.drawable.face_hungry
    );
    private List<Integer> hatList = Arrays.asList(
            R.drawable.hat_halloween, R.drawable.hat_hiphop, R.drawable.hat_onepiece,
            R.drawable.hat_crown, R.drawable.hat_rabbit
    );
    private List<Integer> clothesList = Arrays.asList(
            R.drawable.clothes_halloween, R.drawable.clothes_hiphop, R.drawable.clothes_onepiece,
            R.drawable.clothes_pokemon, R.drawable.clothes_santa
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_right, container, false);

        // 1. 뷰 연결
        imageGrid = view.findViewById(R.id.image_grid);
        faceImage = view.findViewById(R.id.face_image);
        hatImage = view.findViewById(R.id.hat_image);
        clothesImage = view.findViewById(R.id.clothes_image);
        FrameLayout topArea = view.findViewById(R.id.top_area);
        Button btnReset = view.findViewById(R.id.btn_reset);

        // 2. 캐릭터 영역 드롭 리스너 설정
        topArea.setOnDragListener((v, event) -> {
            if (event.getAction() == DragEvent.ACTION_DROP) {
                ClipData.Item item = event.getClipData().getItemAt(0);
                int resId = Integer.parseInt(item.getText().toString());
                updateCharacter(resId);
            }
            return true;
        });

        // 3. 초기화 버튼 리스너
        btnReset.setOnClickListener(v -> resetCharacter());

        // 4. 카테고리 버튼 설정
        view.findViewById(R.id.btn_all).setOnClickListener(v -> showImages(getAllImages()));
        view.findViewById(R.id.btn_face).setOnClickListener(v -> showImages(faceList));
        view.findViewById(R.id.btn_hat).setOnClickListener(v -> showImages(hatList));
        view.findViewById(R.id.btn_top).setOnClickListener(v -> showImages(clothesList));

        // 초기 화면 설정
        showImages(getAllImages());

        return view;
    }

    private List<Integer> getAllImages() {
        List<Integer> all = new ArrayList<>();
        all.addAll(faceList);
        all.addAll(hatList);
        all.addAll(clothesList);
        return all;
    }

    private void showImages(List<Integer> imageList) {
        if (imageGrid == null) return;
        imageGrid.removeAllViews();

        for (int resId : imageList) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(resId);
            imageView.setTag(resId);

            // 드래그 설정
            imageView.setOnLongClickListener(v -> {
                // 부모 뷰(ViewPager 등)가 드래그를 가로채지 못하게 방지
                v.getParent().requestDisallowInterceptTouchEvent(true);

                ClipData data = ClipData.newPlainText("resId", v.getTag().toString());
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, null, 0);
                return true;
            });

            // 그리드 아이템 레이아웃 설정 (높이 350px 고정)
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 350;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(10, 10, 10, 10);

            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setBackgroundResource(android.R.color.transparent);

            imageGrid.addView(imageView);
        }
    }

    private void updateCharacter(int resId) {
        if (faceList.contains(resId)) {
            faceImage.setImageResource(resId);
        } else if (hatList.contains(resId)) {
            hatImage.setImageResource(resId);
        } else if (clothesList.contains(resId)) {
            clothesImage.setImageResource(resId);
        }
    }

    private void resetCharacter() {
        faceImage.setImageResource(R.drawable.face_default);
        hatImage.setImageDrawable(null);
        clothesImage.setImageDrawable(null);
    }
}