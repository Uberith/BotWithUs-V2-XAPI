package net.botwithus.xapi.script.permissive;

public class EvaluationResult<T> {
    private final T result;
    private final int expirationTime;
    private final long resultTime;
    private boolean holding = false;
    private ResultType lastResultType = ResultType.EXPIRED;

    public EvaluationResult(T result) {
        this.result = result;
        this.expirationTime = 2000;
        this.resultTime = System.currentTimeMillis();
    }

    public EvaluationResult(T result, int expirationTime) {
        this.result = result;
        this.expirationTime = expirationTime;
        this.resultTime = System.currentTimeMillis();
    }

    public boolean isValid() {
        return System.currentTimeMillis() - resultTime <= expirationTime;
    }

    public T getResult() {
        return result;
    }

    public ResultType getResultType() {
        if (holding) {
            return lastResultType;
        }
        lastResultType = !isValid() ? ResultType.EXPIRED : ResultType.getResult((boolean)result);
        return lastResultType;
    }

    public boolean isHolding() {
        return holding;
    }

    public void setHolding(boolean holding) {
        this.holding = holding;
    }
}
