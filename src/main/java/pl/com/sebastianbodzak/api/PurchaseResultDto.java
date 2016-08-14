package pl.com.sebastianbodzak.api;

/**
 * Created by Dell on 2016-08-13.
 */
public class PurchaseResultDto {

    private boolean success;
    private String failureReason;

    public PurchaseResultDto() {
        success = true;
    }

    public PurchaseResultDto(String failureReason) {
        this.failureReason = failureReason;
        success = false;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
