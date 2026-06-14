package com.rmh.itemmagnet.item;

public record FuelTransferResult(FuelTransferStatus status, int boostDurationSeconds) {

    public static FuelTransferResult success(int boostDurationSeconds) {
        return new FuelTransferResult(FuelTransferStatus.SUCCESS, boostDurationSeconds);
    }

    public static FuelTransferResult of(FuelTransferStatus status) {
        return new FuelTransferResult(status, 0);
    }

    public boolean transferred() {
        return status == FuelTransferStatus.SUCCESS;
    }
}
