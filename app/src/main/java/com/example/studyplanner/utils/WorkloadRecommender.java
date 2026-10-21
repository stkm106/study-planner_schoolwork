package com.example.studyplanner.utils;

import com.example.studyplanner.data.model.Schedule;
import com.example.studyplanner.data.model.Task;

import java.util.ArrayList;
import java.util.List;

public class WorkloadRecommender {

    public static List<Schedule> generateSuggestedSchedules(List<Schedule> existingSchedules) {
        List<Schedule> recommendations = new ArrayList<>();

        for (int day = 2; day <= 6; day += 2) { // Thứ 2, Thứ 4, Thứ 6
            boolean isSlotBusy = false;
            if (existingSchedules != null) {
                for (Schedule s : existingSchedules) {
                    if (s.getDayOfWeek() == day && "19:00".equals(s.getStartTime())) {
                        isSlotBusy = true;
                        break;
                    }
                }
            }
            if (!isSlotBusy) {
                recommendations.add(new Schedule(null, day, "19:00", "21:00", true));
            }
        }

        return recommendations;
    }

    public static List<Task> convertSchedulesToTasks(List<Schedule> schedules, String selectedDateStr) {
        List<Task> tasks = new ArrayList<>();
        for (Schedule s : schedules) {
            Task task = new Task();
            task.setTitle("[Gợi ý AI] Ôn tập Thứ " + s.getDayOfWeek());
            task.setDescription("Khung giờ vàng đề xuất: " + s.getStartTime() + " - " + s.getEndTime());
            task.setCategory("AUTO");
            task.setStatus(0);
            task.setPriority(1);
            task.setDeadline(selectedDateStr + " " + s.getStartTime());
            task.setLocation("Phòng tự học / Thư viện");
            tasks.add(task);
        }
        return tasks;
    }
}