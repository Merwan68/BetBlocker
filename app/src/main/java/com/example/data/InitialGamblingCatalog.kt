package com.example.data

import com.example.data.model.BlockedAppEntity
import com.example.data.model.BlockedDomainEntity
import com.example.data.model.GamblingCategory

object InitialGamblingCatalog {

    const val INITIAL_CATALOG_VERSION = 1L

    val defaultDomains: List<BlockedDomainEntity> by lazy {
        listOf(
            // Sports Betting
            BlockedDomainEntity("bet365.com", "Bet365", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("draftkings.com", "DraftKings", GamblingCategory.SPORTS_BETTING.name, "US"),
            BlockedDomainEntity("fanduel.com", "FanDuel", GamblingCategory.SPORTS_BETTING.name, "US"),
            BlockedDomainEntity("betmgm.com", "BetMGM", GamblingCategory.SPORTS_BETTING.name, "US"),
            BlockedDomainEntity("caesars.com", "Caesars Sportsbook", GamblingCategory.SPORTS_BETTING.name, "US"),
            BlockedDomainEntity("bovada.lv", "Bovada", GamblingCategory.SPORTS_BETTING.name, "US"),
            BlockedDomainEntity("williamhill.com", "William Hill", GamblingCategory.SPORTS_BETTING.name, "UK"),
            BlockedDomainEntity("unibet.com", "Unibet", GamblingCategory.SPORTS_BETTING.name, "EU"),
            BlockedDomainEntity("betway.com", "Betway", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("pinnacle.com", "Pinnacle Sports", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("1xbet.com", "1xBet", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("bwin.com", "Bwin", GamblingCategory.SPORTS_BETTING.name, "EU"),
            BlockedDomainEntity("paddypower.com", "Paddy Power", GamblingCategory.SPORTS_BETTING.name, "UK"),
            BlockedDomainEntity("skybet.com", "Sky Bet", GamblingCategory.SPORTS_BETTING.name, "UK"),
            BlockedDomainEntity("ladbrokes.com", "Ladbrokes", GamblingCategory.SPORTS_BETTING.name, "UK"),
            BlockedDomainEntity("coral.co.uk", "Coral", GamblingCategory.SPORTS_BETTING.name, "UK"),
            BlockedDomainEntity("pointsbet.com", "PointsBet", GamblingCategory.SPORTS_BETTING.name, "US"),
            BlockedDomainEntity("betrivers.com", "BetRivers", GamblingCategory.SPORTS_BETTING.name, "US"),
            BlockedDomainEntity("betonline.ag", "BetOnline", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("sportsbet.com.au", "Sportsbet", GamblingCategory.SPORTS_BETTING.name, "AU"),
            BlockedDomainEntity("betclic.com", "Betclic", GamblingCategory.SPORTS_BETTING.name, "EU"),

            // Cryptocurrency Gambling
            BlockedDomainEntity("stake.com", "Stake", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("roobet.com", "Roobet", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("rollbit.com", "Rollbit", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("bc.game", "BC.Game", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("duelbits.com", "Duelbits", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("bitstarz.com", "BitStarz", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("shuffle.com", "Shuffle", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("cloudbet.com", "Cloudbet", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("gamdom.com", "Gamdom", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("hypedrop.com", "HypeDrop", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),

            // Online Casino
            BlockedDomainEntity("888casino.com", "888 Casino", GamblingCategory.ONLINE_CASINO.name, "GLOBAL"),
            BlockedDomainEntity("leovegas.com", "LeoVegas", GamblingCategory.ONLINE_CASINO.name, "EU"),
            BlockedDomainEntity("betfaircasino.com", "Betfair Casino", GamblingCategory.ONLINE_CASINO.name, "UK"),
            BlockedDomainEntity("spinpalace.com", "Spin Palace", GamblingCategory.ONLINE_CASINO.name, "GLOBAL"),
            BlockedDomainEntity("jackpotcitycasino.com", "JackpotCity", GamblingCategory.ONLINE_CASINO.name, "GLOBAL"),
            BlockedDomainEntity("casumo.com", "Casumo", GamblingCategory.ONLINE_CASINO.name, "EU"),
            BlockedDomainEntity("ignitioncasino.eu", "Ignition Casino", GamblingCategory.ONLINE_CASINO.name, "GLOBAL"),
            BlockedDomainEntity("cafecasino.lv", "Cafe Casino", GamblingCategory.ONLINE_CASINO.name, "US"),
            BlockedDomainEntity("mrgreen.com", "Mr Green", GamblingCategory.ONLINE_CASINO.name, "EU"),
            BlockedDomainEntity("rizk.com", "Rizk Casino", GamblingCategory.ONLINE_CASINO.name, "EU"),
            BlockedDomainEntity("golden塊casino.com", "Golden Casino", GamblingCategory.ONLINE_CASINO.name, "GLOBAL"),

            // Poker
            BlockedDomainEntity("pokerstars.com", "PokerStars", GamblingCategory.POKER.name, "GLOBAL"),
            BlockedDomainEntity("ggpoker.com", "GGPoker", GamblingCategory.POKER.name, "GLOBAL"),
            BlockedDomainEntity("partypoker.com", "PartyPoker", GamblingCategory.POKER.name, "GLOBAL"),
            BlockedDomainEntity("wsop.com", "WSOP", GamblingCategory.POKER.name, "US"),
            BlockedDomainEntity("americascardroom.eu", "Americas Cardroom", GamblingCategory.POKER.name, "US"),
            BlockedDomainEntity("888poker.com", "888 Poker", GamblingCategory.POKER.name, "GLOBAL"),
            BlockedDomainEntity("wptglobal.com", "WPT Global", GamblingCategory.POKER.name, "GLOBAL"),

            // Betting Exchange
            BlockedDomainEntity("betfair.com", "Betfair Exchange", GamblingCategory.BETTING_EXCHANGE.name, "UK"),
            BlockedDomainEntity("smarkets.com", "Smarkets", GamblingCategory.BETTING_EXCHANGE.name, "UK"),
            BlockedDomainEntity("matchbook.com", "Matchbook", GamblingCategory.BETTING_EXCHANGE.name, "UK"),
            BlockedDomainEntity("betdaq.com", "Betdaq", GamblingCategory.BETTING_EXCHANGE.name, "UK"),

            // Lottery & Slots
            BlockedDomainEntity("thelotter.com", "The Lotter", GamblingCategory.LOTTERY.name, "GLOBAL"),
            BlockedDomainEntity("lotto24.com", "Lotto24", GamblingCategory.LOTTERY.name, "EU"),
            BlockedDomainEntity("chumbacasino.com", "Chumba Casino", GamblingCategory.SLOTS.name, "US"),
            BlockedDomainEntity("luckylandslots.com", "LuckyLand Slots", GamblingCategory.SLOTS.name, "US"),
            BlockedDomainEntity("pulsz.com", "Pulsz Social Casino", GamblingCategory.SLOTS.name, "US")
        )
    }

    val defaultApps: List<BlockedAppEntity> by lazy {
        listOf(
            BlockedAppEntity(
                packageName = "com.draftkings.sportsbook",
                appName = "DraftKings Sportsbook",
                company = "DraftKings Inc.",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "US"
            ),
            BlockedAppEntity(
                packageName = "com.fanduel.sportsbook",
                appName = "FanDuel Sportsbook",
                company = "FanDuel Group",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "US"
            ),
            BlockedAppEntity(
                packageName = "com.betmgm.sports",
                appName = "BetMGM Sportsbook",
                company = "BetMGM",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "US"
            ),
            BlockedAppEntity(
                packageName = "com.williamhill.sports",
                appName = "William Hill Sports",
                company = "William Hill",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "UK"
            ),
            BlockedAppEntity(
                packageName = "com.bet365.sports",
                appName = "Bet365 Sports Betting",
                company = "Hillside (New Media) Ltd",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "GLOBAL"
            ),
            BlockedAppEntity(
                packageName = "com.pokerstars.net",
                appName = "PokerStars",
                company = "Flutter Entertainment",
                category = GamblingCategory.POKER.name,
                country = "GLOBAL"
            ),
            BlockedAppEntity(
                packageName = "com.pokerstars.realmoney",
                appName = "PokerStars Real Money",
                company = "Flutter Entertainment",
                category = GamblingCategory.POKER.name,
                country = "GLOBAL"
            ),
            BlockedAppEntity(
                packageName = "com.betfair.sports",
                appName = "Betfair Sportsbook & Exchange",
                company = "Betfair Ltd",
                category = GamblingCategory.BETTING_EXCHANGE.name,
                country = "UK"
            ),
            BlockedAppEntity(
                packageName = "com.unibet.sportsbook",
                appName = "Unibet Sports Betting",
                company = "Kindred Group",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "EU"
            ),
            BlockedAppEntity(
                packageName = "com.caesars.sportsbook",
                appName = "Caesars Sportsbook",
                company = "Caesars Entertainment",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "US"
            ),
            BlockedAppEntity(
                packageName = "com.betway.sports",
                appName = "Betway Sports Betting",
                company = "Super Group",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "GLOBAL"
            ),
            BlockedAppEntity(
                packageName = "com.bwin.sports",
                appName = "Bwin Sports Betting",
                company = "Entain",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "EU"
            ),
            BlockedAppEntity(
                packageName = "com.ggpoker.app",
                appName = "GGPoker",
                company = "NSUS Group",
                category = GamblingCategory.POKER.name,
                country = "GLOBAL"
            ),
            BlockedAppEntity(
                packageName = "com.eighteighteight.casino",
                appName = "888 Casino Real Money",
                company = "888 Holdings",
                category = GamblingCategory.ONLINE_CASINO.name,
                country = "GLOBAL"
            ),
            BlockedAppEntity(
                packageName = "com.leovegas.casino",
                appName = "LeoVegas Casino",
                company = "LeoVegas Gaming",
                category = GamblingCategory.ONLINE_CASINO.name,
                country = "EU"
            ),
            BlockedAppEntity(
                packageName = "com.roobet.mobile",
                appName = "Roobet Casino",
                company = "Raw Entertainment",
                category = GamblingCategory.CRYPTO_GAMBLING.name,
                country = "GLOBAL"
            ),
            BlockedAppEntity(
                packageName = "com.stake.mobile",
                appName = "Stake Gambling",
                company = "Medium Rare N.V.",
                category = GamblingCategory.CRYPTO_GAMBLING.name,
                country = "GLOBAL"
            ),
            BlockedAppEntity(
                packageName = "com.chumba.casino",
                appName = "Chumba Casino Slots",
                company = "VGW Holdings",
                category = GamblingCategory.SLOTS.name,
                country = "US"
            )
        )
    }
}
