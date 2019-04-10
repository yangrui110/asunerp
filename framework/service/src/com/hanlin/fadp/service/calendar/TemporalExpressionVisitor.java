
package com.hanlin.fadp.service.calendar;

/** Temporal expression visitor interface. */
public interface TemporalExpressionVisitor {
    void visit(TemporalExpressions.DateRange expr);
    void visit(TemporalExpressions.DayInMonth expr);
    void visit(TemporalExpressions.DayOfMonthRange expr);
    void visit(TemporalExpressions.DayOfWeekRange expr);
    void visit(TemporalExpressions.Difference expr);
    void visit(TemporalExpressions.Frequency expr);
    void visit(TemporalExpressions.HourRange expr);
    void visit(TemporalExpressions.Intersection expr);
    void visit(TemporalExpressions.MinuteRange expr);
    void visit(TemporalExpressions.MonthRange expr);
    void visit(TemporalExpressions.Null expr);
    void visit(TemporalExpressions.Substitution expr);
    void visit(TemporalExpressions.Union expr);
}
