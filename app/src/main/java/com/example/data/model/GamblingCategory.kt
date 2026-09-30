package com.example.data.model

enum class GamblingCategory(val displayName: String) {
    SPORTS_BETTING("Sports Betting"),
    ONLINE_CASINO("Online Casino"),
    LOTTERY("Lottery"),
    POKER("Poker"),
    SLOTS("Slots"),
    GAMBLING_GAMES("Gambling Games"),
    BETTING_EXCHANGE("Betting Exchange"),
    CRYPTO_GAMBLING("Cryptocurrency Gambling"),
    OTHER_GAMBLING("Other Gambling");

    companion object {
        fun fromString(value: String): GamblingCategory {
            return entries.firstOrNull {
                it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true)
            } ?: OTHER_GAMBLING
        }
    }
}
