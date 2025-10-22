package btl.ballgame.client.ui.menu;

import java.util.ArrayList;
import java.util.List;

public class HistoryScore {
    private int max_score;
    private int min_score;
    private final List<Integer> history_score;
    private int size;

    public HistoryScore() {
        this.history_score = new ArrayList<>();
        this.max_score = 0;
        this.size = 0;
    }

    /**
     * thêm score, nếu có 1 score thì gán luôn min score = cái đầu tiên để so sánh
     * @param score
     */
    public void add_score(int score) {
        this.history_score.add(score);
        size++;
        if (size == 1) {
            this.min_score = history_score.get(1);
        }
        updateMaxScore();

    }

    private void updateMaxScore() {
        for(int i = 0; i < size; i++) {
            if(history_score.get(i) > max_score) {
                max_score = history_score.get(i);
            }
        }
    }

    public int getMaxScore() {
        return this.max_score;
    }

    public int getMinScore() {
        return this.min_score;
    }

}
