package com.example.data

import com.example.data.model.BlockedAppEntity
import com.example.data.model.BlockedDomainEntity
import com.example.data.model.GamblingCategory

object InitialGamblingCatalog {

    const val INITIAL_CATALOG_VERSION = 2L

    val defaultDomains: List<BlockedDomainEntity> by lazy {
        listOf(
            // --- Specific User-Requested & Regional Sports Betting Platforms ---
            BlockedDomainEntity("dash.bet", "DashBet", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("dashbet.com", "DashBet Global", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("dashbet.et", "DashBet Ethiopia", GamblingCategory.SPORTS_BETTING.name, "ET"),
            BlockedDomainEntity("dashbet.net", "DashBet Network", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),

            BlockedDomainEntity("zplaybet.xyz", "Zplay Bet XYZ", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("zplay.bet", "Zplay Bet", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("zplaybet.com", "ZplayBet", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("zplay.et", "Zplay Ethiopia", GamblingCategory.SPORTS_BETTING.name, "ET"),

            BlockedDomainEntity("melbet.com", "Melbet", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("melbet.org", "Melbet Org", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("melbet.et", "Melbet Ethiopia", GamblingCategory.SPORTS_BETTING.name, "ET"),
            BlockedDomainEntity("melbet-et.com", "Melbet ET", GamblingCategory.SPORTS_BETTING.name, "ET"),
            BlockedDomainEntity("melbet.co", "Melbet Co", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),

            BlockedDomainEntity("1xbet.com", "1xBet", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("1xbet.mobi", "1xBet Mobile", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("1xbet.et", "1xBet Ethiopia", GamblingCategory.SPORTS_BETTING.name, "ET"),
            BlockedDomainEntity("1x-bet.com", "1x-Bet", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("1xbet-et.com", "1xBet ET", GamblingCategory.SPORTS_BETTING.name, "ET"),

            BlockedDomainEntity("mbs.bet", "MBS Bet", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("mbsbet.com", "MBS Betting", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("mbsbet.et", "MBS Bet Ethiopia", GamblingCategory.SPORTS_BETTING.name, "ET"),

            BlockedDomainEntity("habeshabet.com", "HabeshaBet", GamblingCategory.SPORTS_BETTING.name, "ET"),
            BlockedDomainEntity("habesha.bet", "Habesha.Bet", GamblingCategory.SPORTS_BETTING.name, "ET"),
            BlockedDomainEntity("vamos.bet", "VamosBet", GamblingCategory.SPORTS_BETTING.name, "ET"),
            BlockedDomainEntity("vamosbet.et", "VamosBet Ethiopia", GamblingCategory.SPORTS_BETTING.name, "ET"),
            BlockedDomainEntity("vamosbet.com", "VamosBet Global", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("hulusport.com", "HuluSport", GamblingCategory.SPORTS_BETTING.name, "ET"),
            BlockedDomainEntity("hulusport.et", "HuluSport Ethiopia", GamblingCategory.SPORTS_BETTING.name, "ET"),
            BlockedDomainEntity("winner.et", "Winner.et", GamblingCategory.SPORTS_BETTING.name, "ET"),
            BlockedDomainEntity("winnerbet.et", "WinnerBet Ethiopia", GamblingCategory.SPORTS_BETTING.name, "ET"),
            BlockedDomainEntity("galaxybet.et", "GalaxyBet", GamblingCategory.SPORTS_BETTING.name, "ET"),
            BlockedDomainEntity("galaxybetting.com", "Galaxy Betting", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("bravobet.et", "BravoBet", GamblingCategory.SPORTS_BETTING.name, "ET"),
            BlockedDomainEntity("bravobet.com", "BravoBet Global", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("betika.com", "Betika", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("betika.et", "Betika Ethiopia", GamblingCategory.SPORTS_BETTING.name, "ET"),
            BlockedDomainEntity("sportpesa.com", "SportPesa", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("sportpesa.co.ke", "SportPesa Kenya", GamblingCategory.SPORTS_BETTING.name, "KE"),
            BlockedDomainEntity("sportybet.com", "SportyBet", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("bet9ja.com", "Bet9ja", GamblingCategory.SPORTS_BETTING.name, "NG"),
            BlockedDomainEntity("helabet.com", "Helabet", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("megapari.com", "Megapari", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("parimatch.com", "Parimatch", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("22bet.com", "22Bet", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("betwinner.com", "BetWinner", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("linebet.com", "LineBet", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("mostbet.com", "MostBet", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),
            BlockedDomainEntity("betpawa.com", "BetPawa", GamblingCategory.SPORTS_BETTING.name, "GLOBAL"),

            // --- Major Global Sports Betting Platforms ---
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

            // --- Cryptocurrency Gambling & Crash Games ---
            BlockedDomainEntity("stake.com", "Stake", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("stake.bet", "Stake Bet", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("roobet.com", "Roobet", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("rollbit.com", "Rollbit", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("bc.game", "BC.Game", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("duelbits.com", "Duelbits", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("bitstarz.com", "BitStarz", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("shuffle.com", "Shuffle", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("cloudbet.com", "Cloudbet", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("gamdom.com", "Gamdom", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("hypedrop.com", "HypeDrop", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("csgoroll.com", "CSGORoll", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),
            BlockedDomainEntity("csgoempire.com", "CSGOEmpire", GamblingCategory.CRYPTO_GAMBLING.name, "GLOBAL"),

            // --- Online Casino ---
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

            // --- Poker ---
            BlockedDomainEntity("pokerstars.com", "PokerStars", GamblingCategory.POKER.name, "GLOBAL"),
            BlockedDomainEntity("ggpoker.com", "GGPoker", GamblingCategory.POKER.name, "GLOBAL"),
            BlockedDomainEntity("partypoker.com", "PartyPoker", GamblingCategory.POKER.name, "GLOBAL"),
            BlockedDomainEntity("wsop.com", "WSOP", GamblingCategory.POKER.name, "US"),
            BlockedDomainEntity("americascardroom.eu", "Americas Cardroom", GamblingCategory.POKER.name, "US"),
            BlockedDomainEntity("888poker.com", "888 Poker", GamblingCategory.POKER.name, "GLOBAL"),
            BlockedDomainEntity("wptglobal.com", "WPT Global", GamblingCategory.POKER.name, "GLOBAL"),

            // --- Betting Exchange ---
            BlockedDomainEntity("betfair.com", "Betfair Exchange", GamblingCategory.BETTING_EXCHANGE.name, "UK"),
            BlockedDomainEntity("smarkets.com", "Smarkets", GamblingCategory.BETTING_EXCHANGE.name, "UK"),
            BlockedDomainEntity("matchbook.com", "Matchbook", GamblingCategory.BETTING_EXCHANGE.name, "UK"),
            BlockedDomainEntity("betdaq.com", "Betdaq", GamblingCategory.BETTING_EXCHANGE.name, "UK"),

            // --- Lottery & Slots ---
            BlockedDomainEntity("thelotter.com", "The Lotter", GamblingCategory.LOTTERY.name, "GLOBAL"),
            BlockedDomainEntity("lotto24.com", "Lotto24", GamblingCategory.LOTTERY.name, "EU"),
            BlockedDomainEntity("chumbacasino.com", "Chumba Casino", GamblingCategory.SLOTS.name, "US"),
            BlockedDomainEntity("luckylandslots.com", "LuckyLand Slots", GamblingCategory.SLOTS.name, "US"),
            BlockedDomainEntity("pulsz.com", "Pulsz Social Casino", GamblingCategory.SLOTS.name, "US")
        )
    }

    val defaultApps: List<BlockedAppEntity> by lazy {
        listOf(
            // Regional & Specific User-Requested Betting Apps
            BlockedAppEntity(
                packageName = "org.melbet.app",
                appName = "Melbet Sports",
                company = "Melbet",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "GLOBAL"
            ),
            BlockedAppEntity(
                packageName = "com.melbet.android",
                appName = "Melbet Android",
                company = "Melbet",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "GLOBAL"
            ),
            BlockedAppEntity(
                packageName = "com.onexbet.mobile",
                appName = "1xBet Mobile",
                company = "1xCorp N.V.",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "GLOBAL"
            ),
            BlockedAppEntity(
                packageName = "com.dashbet.app",
                appName = "DashBet",
                company = "DashBet",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "GLOBAL"
            ),
            BlockedAppEntity(
                packageName = "com.zplay.bet",
                appName = "Zplay Bet",
                company = "Zplay",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "GLOBAL"
            ),
            BlockedAppEntity(
                packageName = "com.habeshabet.app",
                appName = "HabeshaBet",
                company = "HabeshaBet",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "ET"
            ),
            BlockedAppEntity(
                packageName = "com.vamosbet.app",
                appName = "VamosBet",
                company = "VamosBet",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "ET"
            ),
            BlockedAppEntity(
                packageName = "com.hulusport.app",
                appName = "HuluSport",
                company = "HuluSport",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "ET"
            ),
            BlockedAppEntity(
                packageName = "com.sportybet.android",
                appName = "SportyBet",
                company = "Sporty Group",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "GLOBAL"
            ),
            BlockedAppEntity(
                packageName = "com.betika.mobile",
                appName = "Betika Sportsbook",
                company = "Betika",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "GLOBAL"
            ),
            BlockedAppEntity(
                packageName = "com.parimatch.sports",
                appName = "Parimatch Sports",
                company = "Parimatch",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "GLOBAL"
            ),
            BlockedAppEntity(
                packageName = "com.twentytwobet.app",
                appName = "22Bet Sports",
                company = "22Bet",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "GLOBAL"
            ),
            BlockedAppEntity(
                packageName = "com.betwinner.app",
                appName = "BetWinner",
                company = "BetWinner",
                category = GamblingCategory.SPORTS_BETTING.name,
                country = "GLOBAL"
            ),

            // Major Global Betting Apps
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
