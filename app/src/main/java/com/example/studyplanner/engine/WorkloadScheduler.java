package com.example.studyplanner.engine;

import com.example.studyplanner.data.TaskEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkloadScheduler {

    public static List<TaskEntity> generateAutoSchedule(List<TaskEntity> upcomingExamsAndAssignments) {
        List<TaskEntity> generatedSchedule = new ArrayList<>();
        long now = System.currentTimeMillis();

        Collections.sort(upcomingExamsAndAssignments, (t1, t2) -> {
            double score1 = calculateWorkloadScore(t1, now);
            double score2 = calculateWorkloadScore(t2, now);
            return Double.compare(score2, score1);
        });

        for (TaskEntity item : upcomingExamsAndAssignments) {
            long daysRemaining = (item.dateTimeMillis - now) / (1000 * 60 * 60 * 24);
            if (daysRemaining <= 0) continue;

            int totalMinutesNeeded = item.durationMinutes;
            int minutesPerSession = Math.min(60, (int) Math.ceil((double) totalMinutesNeeded / daysRemaining));

            for (int i = 0; i < daysRemaining && totalMinutesNeeded > 0; i++) {
                long sessionDateMs = now + ((long) i * 24 * 60 * 60 * 1000);
                int sessionDuration = Math.min(minutesPerSession, totalMinutesNeeded);

                TaskEntity studySession = new TaskEntity(
                        "Ôn tập [GK]: " + item.title,
                        "Buổi học tự động đề xuất dựa trên khối lượng công việc",
                        sessionDateMs,
                        "STUDY",
                        sessionDuration,
                        item.priority
                );

                generatedSchedule.add(studySession);
                totalMinutesNeeded -= sessionDuration;
            }
        }

        return generatedSchedule;
    }

    private static double calculateWorkloadScore(TaskEntity task, long currentTimestamp) {
        double hoursRemaining = Math.max(1.0, (task.dateTimeMillis - currentTimestamp) / (1000.0 * 60 * 60));
        return (task.priority * task.durationMinutes) / hoursRemaining;
    }
}