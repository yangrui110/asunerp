
package com.hanlin.fadp.service.calendar;

/** Temporal expression pretty printer. */
public class TemporalExpressionPrinter implements TemporalExpressionVisitor {
    protected final TemporalExpression expression;
    protected final StringBuilder sb = new StringBuilder();
    protected int indentSize = 2;
    protected int currentIndent = 0;
    
    public TemporalExpressionPrinter(TemporalExpression expression) {
        this.expression = expression;
    }

    public TemporalExpressionPrinter(TemporalExpression expression, int indentSize) {
        this.expression = expression;
        if (indentSize > 0) {
            this.indentSize = indentSize;
        }
    }

    protected void appendExpression(TemporalExpression expression) {
        appendIndent();
        this.sb.append(expression);
        this.sb.append("\n");
    }

    protected void appendIndent() {
        for (int i = 0; i < this.currentIndent; i++) {
            this.sb.append(" ");
        }
    }

    protected void indent() {
        this.currentIndent += this.indentSize;
    }

    @Override
    public String toString() {
        this.expression.accept(this);
        return this.sb.toString();
    }

    protected void unIndent() {
        this.currentIndent -= this.indentSize;
    }

    @Override
    public void visit(TemporalExpressions.DateRange expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(TemporalExpressions.DayInMonth expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(TemporalExpressions.DayOfMonthRange expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(TemporalExpressions.DayOfWeekRange expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(TemporalExpressions.Difference expr) {
        appendIndent();
        this.sb.append("Difference [");
        this.sb.append(expr.getId());
        this.sb.append("]:\n");
        indent();
        appendIndent();
        this.sb.append("Include:\n");
        indent();
        expr.included.accept(this);
        unIndent();
        appendIndent();
        this.sb.append("Exclude:\n");
        indent();
        expr.excluded.accept(this);
        unIndent();
        unIndent();
    }

    @Override
    public void visit(TemporalExpressions.Frequency expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(TemporalExpressions.HourRange expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(TemporalExpressions.Intersection expr) {
        appendIndent();
        this.sb.append("Intersection [");
        this.sb.append(expr.getId());
        this.sb.append("]:\n");
        indent();
        for (TemporalExpression member: expr.expressionSet) {
            member.accept(this);
        }
        unIndent();
    }

    @Override
    public void visit(TemporalExpressions.MinuteRange expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(TemporalExpressions.MonthRange expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(TemporalExpressions.Null expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(TemporalExpressions.Substitution expr) {
        appendIndent();
        this.sb.append("Substitution [");
        this.sb.append(expr.getId());
        this.sb.append("]:\n");
        indent();
        appendIndent();
        this.sb.append("Include:\n");
        indent();
        expr.included.accept(this);
        unIndent();
        appendIndent();
        this.sb.append("Exclude:\n");
        indent();
        expr.excluded.accept(this);
        unIndent();
        appendIndent();
        this.sb.append("Substitute:\n");
        indent();
        expr.substitute.accept(this);
        unIndent();
        unIndent();
    }

    @Override
    public void visit(TemporalExpressions.Union expr) {
        appendIndent();
        this.sb.append("Union [");
        this.sb.append(expr.getId());
        this.sb.append("]:\n");
        indent();
        for (TemporalExpression member: expr.expressionSet) {
            member.accept(this);
        }
        unIndent();
    }
}
