package com.example.studyplanner.utils;

import com.example.studyplanner.data.model.Schedule;

import java.util.ArrayList;
import java.util.List;

public class WorkloadRecommender {

    /**
     * Thuật toán gợi ý các khung giờ học tự động dựa trên các khung giờ chưa đăng ký.
     */
    public static List<Schedule> generateSuggestedSchedules(List<Schedule> existingSchedules) {
        List<Schedule> recommendations = new ArrayList<>();

        // Ví dụ gợi ý bổ sung khung giờ cố định vào Thứ 2 (2) và Thứ 4 (4) nếu chưa bận
        for (int day = 2; day <= 6; day += 2) { // Thứ 2, Thứ 4, Thứ 6
            boolean isSlotBusy = false;
            for (Schedule s : existingSchedules) {
                if (s.getDayOfWeek() == day && "19:00".equals(s.getStartTime())) {
                    isSlotBusy = true;
                    break;
                }
            }
            if (!isSlotBusy) {
                recommendations.add(new Schedule(null, day, "19:00", "21:00", true));
            }
        }

        return recommendations;
    }
}