package com.nftworlds.wrldcommands.offlinepayments;

import java.util.UUID;

final class OfflineNFTPlayer {
    private final UUID uuid;
    private final double payAmount;
    private final String payReason;

    OfflineNFTPlayer(UUID uuid, double payAmount, String payReason) {
        this.uuid = uuid;
        this.payAmount = payAmount;
        this.payReason = payReason;
    }

    UUID getUuid() {
        return uuid;
    }

    double getPayAmount() {
        return payAmount;
    }

    String getPayReason() {
        return payReason;
    }
}
