package com.example.studyplanner.utils;

import com.example.studyplanner.data.model.Task;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkloadScheduler {

    public static List<Task> generateAutoSchedule(List<Task> upcomingExamsAndAssignments) {
        List<Task> generatedSchedule = new ArrayList<>();
        long now = System.currentTimeMillis();

        Collections.sort(upcomingExamsAndAssignments, (t1, t2) -> {
            double score1 = calculateWorkloadScore(t1, now);
            double score2 = calculateWorkloadScore(t2, now);
            return Double.compare(score2, score1);
        });

        for (Task item : upcomingExamsAndAssignments) {
            long daysRemaining = 2; // Mặc định tính giãn cách 2 ngày ôn tập

            for (int i = 0; i < daysRemaining; i++) {
                // Tạo Task bằng Setter siêu an toàn
                Task studySession = new Task();
                studySession.setTitle("Ôn tập [GK]: " + item.getTitle());
                studySession.setDescription("Buổi học tự động đề xuất dựa trên khối lượng công việc");
                studySession.setCategory("AUTO");
                studySession.setStatus(0);
                studySession.setPriority(item.getPriority());
                studySession.setDeadline("Sắp tới");
                studySession.setLocation("Phòng tự học");

                generatedSchedule.add(studySession);
            }
        }

        return generatedSchedule;
    }

    private static double calculateWorkloadScore(Task task, long currentTimestamp) {
        return (task.getPriority() * 60) / 24.0;
    }
}