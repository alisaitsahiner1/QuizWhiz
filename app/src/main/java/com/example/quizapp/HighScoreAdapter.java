package com.example.quizapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HighScoreAdapter extends RecyclerView.Adapter<HighScoreAdapter.ViewHolder> {

    private List<ResulteActivity.HighScore> highScores;

    public HighScoreAdapter(List<ResulteActivity.HighScore> highScores) {
        this.highScores = highScores;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.highscore_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ResulteActivity.HighScore highScore = highScores.get(position);
        holder.textViewName.setText(highScore.name + " " + highScore.surname);
        holder.textViewScore.setText(String.valueOf(highScore.score));
    }

    @Override
    public int getItemCount() {
        return highScores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewScore;

        public ViewHolder(View view) {
            super(view);
            textViewName = view.findViewById(R.id.textViewName);
            textViewScore = view.findViewById(R.id.textViewScore);
        }
    }
}
