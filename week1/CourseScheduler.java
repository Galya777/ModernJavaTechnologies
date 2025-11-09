package modernJava.week1;

public class CourseScheduler {
    public static int maxNonOverlappingCourses(int[][] courses) {
        if (courses == null || courses.length == 0) {
            return 0;
        }

        // Сортираме курсовете по крайно време (втори елемент)
        for (int i = 0; i < courses.length; i++) {
            for (int j = i + 1; j < courses.length; j++) {
                if (courses[i][1] > courses[j][1]) {
                    // Разменяме двата курса
                    int[] temp = courses[i];
                    courses[i] = courses[j];
                    courses[j] = temp;
                }
            }
        }

        int count = 1;
        int lastEnd = courses[0][1];

        for (int i = 1; i < courses.length; i++) {
            if (courses[i][0] >= lastEnd) {
                count++;
                lastEnd = courses[i][1];
            }
        }

        return count;
    }
}

