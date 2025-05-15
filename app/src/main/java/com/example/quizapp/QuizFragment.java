package com.example.quizapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.example.quizapp.databinding.FragmentQuizBinding;
import java.util.Map;

public class QuizFragment extends Fragment {

    private FragmentQuizBinding binding;
    private String correctAnswerKey = "";
    private Button btn_click;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentQuizBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnChoose1.setOnClickListener(this::ClickChoose);
        binding.btnChoose2.setOnClickListener(this::ClickChoose);
        binding.btnChoose3.setOnClickListener(this::ClickChoose);
        binding.btnChoose4.setOnClickListener(this::ClickChoose);

        return view;
    }

    void fillData(PlayActivity.Question question) {
        Map<String, String> choices = question.getChoices();
        binding.btnChoose1.setText(choices.get("a"));
        binding.btnChoose2.setText(choices.get("b"));
        binding.btnChoose3.setText(choices.get("c"));
        binding.btnChoose4.setText(choices.get("d"));

        correctAnswerKey = question.getCorrect();
    }

    public void ClickChoose(View view) {
        btn_click = (Button) view;

        resetButtonColors();
        btn_click.setBackgroundResource(R.drawable.background_btn_choose_color);

        String value = "";
        switch (btn_click.getId()) {
            case R.id.btn_choose1:
                value = "a";
                break;
            case R.id.btn_choose2:
                value = "b";
                break;
            case R.id.btn_choose3:
                value = "c";
                break;
            case R.id.btn_choose4:
                value = "d";
                break;
        }

        PlayActivity activity = (PlayActivity) getActivity();
        if (activity != null) {
            activity.updateValueChoose(value, btn_click);
        }
    }

    public void resetButtonColors() {
        binding.btnChoose1.setBackgroundResource(R.drawable.background_btn_choose);
        binding.btnChoose2.setBackgroundResource(R.drawable.background_btn_choose);
        binding.btnChoose3.setBackgroundResource(R.drawable.background_btn_choose);
        binding.btnChoose4.setBackgroundResource(R.drawable.background_btn_choose);
    }
}
