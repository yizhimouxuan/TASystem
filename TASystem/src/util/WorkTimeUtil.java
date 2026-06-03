package util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for parsing and managing work time slots with specific dates.
 * Format: "2026-05-22 09:00-11:00; 2026-05-24 14:00-16:00"
 * Each slot: YYYY-MM-DD HH:MM-HH:MM
 * Ensures work times are always in the future (after system time).
 * Workload only counts after the slot end time has passed.
 */
public class WorkTimeUtil {

    private WorkTimeUtil() {}

    /** Time slots for dropdown selection (2-hour blocks) */
    public static final String[] TIME_SLOTS = {
        "08:00-10:00", "09:00-11:00", "10:00-12:00",
        "13:00-15:00", "14:00-16:00", "15:00-17:00", "16:00-18:00",
        "18:00-20:00"
    };

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd (EEE)");

    /** Represents a single work time slot with a specific date */
    public static class WorkSlot {
        public String date;      // e.g. "2026-05-22"
        public String timeSlot;  // e.g. "09:00-11:00"

        public WorkSlot(String date, String timeSlot) {
            this.date = date;
            this.timeSlot = timeSlot;
        }

        public LocalDate getLocalDate() {
            return LocalDate.parse(date, DATE_FORMATTER);
        }

        /** Get the end time of this slot as an epoch millis timestamp */
        public long getEndTimestamp() {
            LocalDate d = getLocalDate();
            String endTime = timeSlot.split("-")[1].trim();
            LocalTime t = LocalTime.parse(endTime);
            return d.atTime(t).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }

        /** Get the start time of this slot as an epoch millis timestamp */
        public long getStartTimestamp() {
            LocalDate d = getLocalDate();
            String startTime = timeSlot.split("-")[0].trim();
            LocalTime t = LocalTime.parse(startTime);
            return d.atTime(t).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }

        /** Check if this slot's end time has passed (work is completed) */
        public boolean isPast() {
            return System.currentTimeMillis() > getEndTimestamp();
        }

        /** Check if this slot is in the future (can still be scheduled) */
        public boolean isFuture() {
            return System.currentTimeMillis() < getStartTimestamp();
        }

        @Override
        public String toString() {
            return date + " " + timeSlot;
        }
    }

    /** Get current system time as LocalDate */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    /** Get current system time as epoch millis */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /** Get current year */
    public static int getCurrentYear() {
        return getCurrentDate().getYear();
    }

    /** Get current month (1-12) */
    public static int getCurrentMonth() {
        return getCurrentDate().getMonthValue();
    }

    /** Get current day of month */
    public static int getCurrentDay() {
        return getCurrentDate().getDayOfMonth();
    }

    /** Get current hour (0-23) */
    public static int getCurrentHour() {
        return LocalTime.now().getHour();
    }

    /**
     * Encode a list of work slots into a storage string.
     * e.g. [2026-05-22 09:00-11:00, 2026-05-24 14:00-16:00]
     *    -> "2026-05-22 09:00-11:00; 2026-05-24 14:00-16:00"
     */
    public static String encodeSlots(List<WorkSlot> slots) {
        if (slots == null || slots.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < slots.size(); i++) {
            if (i > 0) sb.append("; ");
            sb.append(slots.get(i).toString());
        }
        return sb.toString();
    }

    /**
     * Decode a storage string into a list of work slots.
     * e.g. "2026-05-22 09:00-11:00; 2026-05-24 14:00-16:00"
     *    -> list of WorkSlot objects
     */
    public static List<WorkSlot> decodeSlots(String workTime) {
        List<WorkSlot> slots = new ArrayList<>();
        if (workTime == null || workTime.trim().isEmpty()) return slots;

        String[] parts = workTime.split(";");
        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) continue;
            // Format: "2026-05-22 09:00-11:00"
            int spaceIdx = part.lastIndexOf(' ');
            if (spaceIdx > 0) {
                String date = part.substring(0, spaceIdx).trim();
                String timeSlot = part.substring(spaceIdx + 1).trim();
                slots.add(new WorkSlot(date, timeSlot));
            }
        }
        return slots;
    }

    /**
     * Check if all slots in the work time string are in the future.
     * Returns true if all slots start after current system time.
     */
    public static boolean isAllFuture(String workTime) {
        List<WorkSlot> slots = decodeSlots(workTime);
        if (slots.isEmpty()) return false;
        for (WorkSlot slot : slots) {
            if (!slot.isFuture()) return false;
        }
        return true;
    }

    /**
     * Check if at least one slot in the work time has ended (past).
     * Used for workload: only count if work has been completed.
     */
    public static boolean hasPastSlots(String workTime) {
        List<WorkSlot> slots = decodeSlots(workTime);
        for (WorkSlot slot : slots) {
            if (slot.isPast()) return true;
        }
        return false;
    }

    /**
     * Check if ALL slots in the work time have ended (past).
     * Used to determine if a job should be marked as ENDED.
     */
    public static boolean isAllPast(String workTime) {
        List<WorkSlot> slots = decodeSlots(workTime);
        if (slots.isEmpty()) return false;
        for (WorkSlot slot : slots) {
            if (!slot.isPast()) return false;
        }
        return true;
    }

    /**
     * Calculate total hours from slots that have already passed.
     * Used for workload report: only count completed work.
     */
    public static int calculateCompletedHours(String workTime) {
        List<WorkSlot> slots = decodeSlots(workTime);
        int total = 0;
        for (WorkSlot slot : slots) {
            if (slot.isPast()) {
                total += getSlotHours(slot.timeSlot);
            }
        }
        return total;
    }

    /**
     * Calculate total hours from ALL slots (for scheduling display).
     */
    public static int calculateTotalHours(String workTime) {
        List<WorkSlot> slots = decodeSlots(workTime);
        int total = 0;
        for (WorkSlot slot : slots) {
            total += getSlotHours(slot.timeSlot);
        }
        return total;
    }

    /**
     * Get hours for a single time slot.
     * e.g. "09:00-11:00" -> 2 hours
     */
    public static int getSlotHours(String timeSlot) {
        if (timeSlot == null || !timeSlot.contains("-")) return 0;
        try {
            String[] range = timeSlot.split("-");
            if (range.length != 2) return 0;
            int start = parseHour(range[0].trim());
            int end = parseHour(range[1].trim());
            return Math.max(0, end - start);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Parse "HH:MM" to hour integer.
     */
    private static int parseHour(String time) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0].trim());
        int minute = Integer.parseInt(parts[1].trim());
        return minute > 30 ? hour + 1 : hour;
    }

    /**
     * Format work time for display.
     * e.g. "2026-05-22 09:00-11:00" -> "2026-05-22 09:00-11:00"
     */
    public static String formatForDisplay(String workTime) {
        if (workTime == null || workTime.isEmpty()) return "-";
        StringBuilder sb = new StringBuilder();
        List<WorkSlot> slots = decodeSlots(workTime);
        for (int i = 0; i < slots.size(); i++) {
            if (i > 0) sb.append("; ");
            WorkSlot slot = slots.get(i);
            sb.append(slot.date).append(" ").append(slot.timeSlot);
        }
        return sb.toString();
    }

    /**
     * Format date as string yyyy-MM-dd.
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    /**
     * Validate that the given date+time slot is after current system time.
     * Returns error message if invalid, null if valid.
     */
    public static String validateSlot(int year, int month, int day, String timeSlot) {
        try {
            LocalDate date = LocalDate.of(year, month, day);
            LocalDate today = getCurrentDate();

            if (date.isBefore(today)) {
                return "Date must be today or in the future.";
            }

            String startTime = timeSlot.split("-")[0].trim();
            LocalTime time = LocalTime.parse(startTime);

            if (date.isEqual(today) && time.getHour() <= getCurrentHour()) {
                return "Time slot must be after current time (" + getCurrentHour() + ":00).";
            }

            return null; // valid
        } catch (Exception e) {
            return "Invalid date or time.";
        }
    }
}
