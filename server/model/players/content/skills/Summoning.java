package server.model.players.content.skills;

import server.Config;
import server.Server;
import server.model.items.Item;
import server.model.npcs.NPCHandler;
import server.model.players.Client;
import server.model.players.content.GabbesAchievements;

public class Summoning {

	Client c;

	private final static int POUCH = 12155;

	public static void makeSummoningPouch(Client c, int usedItem, int usedWith) {
		for (int i = 0; i < summoningPouchData.length; i++) {
			if (usedItem == POUCH) {
				if (usedWith == c.getItems()
						.getItemId(summoningPouchData[i][2])) {
					if (c.getItems().playerHasItem(POUCH)
							&& c.getItems().playerHasItem(SHARD,
									Integer.parseInt(summoningPouchData[i][3]))
							&& c.getItems().playerHasItem(
									c.getItems().getItemId(
											summoningPouchData[i][1]))
							&& c.getItems().playerHasItem(
									c.getItems().getItemId(
											summoningPouchData[i][2]))) {
						if (c.playerLevel[21] >= Integer
								.parseInt(summoningPouchData[i][4])) {
							c.getItems().deleteItem(POUCH, 1);
							c.getItems().deleteItem(SHARD,
									Integer.parseInt(summoningPouchData[i][3]));
							c.getItems().deleteItem(
									c.getItems().getItemId(
											summoningPouchData[i][1]), 1);
							c.getItems().deleteItem(
									c.getItems().getItemId(
											summoningPouchData[i][2]), 1);
							c.getItems().addItem(
									c.getItems().getItemId(
											summoningPouchData[i][0]), 1);
							c.getPA()
									.addSkillXP(
											Integer.parseInt(summoningPouchData[i][4]) * 900,
											21);
							break;
						} else {
							c.sendMessage("You do not have the required level to make this pouch");
							break;
						}
					} else {
						c.sendMessage("You do not have the required items to make this pouch.");
						c.sendMessage("You need: " + summoningPouchData[i][3]
								+ " Shards ");
						c.sendMessage("You need a " + summoningPouchData[i][1]
								+ " and a  " + summoningPouchData[i][2] + "");
						break;
					}
				}
			}
		}
	}

	public static void openInterface(Client c) {
		c.isBanking = true;
		c.storing = true;
		c.getItems().resetItems(5063);
		c.getItems().resetTempItems();
		refreshInterface(c);
		c.getPA().interfaceWithInventory(2700, 5063);
	}

	public static void refreshInterface(Client c) {
		for (int k = 0; k < 29; k++) {
			if (c.storeditems[k] > 0) {
				c.getPA().Frame34(2702, c.storeditems[k], k, 1);
			}

			if (c.storeditems[k] <= 0) {
				c.getPA().Frame34(2702, -1, k, 1);
			}

		}
	}

	public static void storeItem(Client c, int itemID, int fromSlot, int amount) {
		if (c.storing) {
			if (c.occupied[c.summoningslot] == false
					&& c.maxstore > c.summoningslot
					&& c.maxstore != c.totalstored
					&& !c.getItems().isStackable(itemID)) {
				for (int i : Config.ITEM_SELLABLE) {
					if (i == itemID) {
						c.sendMessage("You can't store untradeable item: "
								+ c.getItems().getItemName(itemID)
										.toLowerCase() + ".");
						openInterface(c);
						return;
					}
				}

				for (int i : Config.ITEM_TRADEABLE) {
					if (i == itemID) {
						c.sendMessage("You can't store untradeable item: "
								+ c.getItems().getItemName(itemID)
										.toLowerCase() + ".");
						openInterface(c);
						return;
					}
				}

				for (int i : Config.RUNE_ECC) {
					if (i == itemID) {
						c.sendMessage("You can't store untradeable item: "
								+ c.getItems().getItemName(itemID)
										.toLowerCase() + ".");
						openInterface(c);
						return;
					}
				}

				if (c.getItems().getItemAmount(itemID) < amount) {
					amount = c.getItems().getItemAmount(itemID);
					openInterface(c);
				}

				if (Item.itemStackable[itemID]) {
					c.sendMessage("You can't store - Stackable items.");
					openInterface(c);
					return;
				}

				if (itemID == 995) {
					c.sendMessage("You can't store money.");
					openInterface(c);
					return;
				}
				if (itemID == 1436) {
					c.sendMessage("You can't store - Rune eccense.");
					openInterface(c);
					return;
				}

				if (!c.getItems().playerHasItem(itemID, amount, fromSlot)) {
					openInterface(c);
					return;
				}

				if (c.maxstore() <= c.totalstored()) {
					c.sendMessage("You cannot store anymore");
					openInterface(c);
					return;
				}

				if (c.getItems().isStackable(itemID)) {

					c.firstSlot(itemID);
					if (c.maxstore() <= c.totalstored()) {
						c.sendMessage("You cannot store anymore");
						openInterface(c);
						return;
					}

					c.getPA().Frame34(2702, itemID, c.summoningslot, amount);
					c.getItems().deleteItem(itemID, amount);
					c.storeditems[c.summoningslot] = itemID;
					if (c.amount[c.summoningslot] < 0) {
						c.amount[c.summoningslot] = 0;
					}
					c.amount[c.summoningslot] = c.amount[c.summoningslot]
							+ amount;
					c.getBank().resetTempItems();
					c.occupied[c.summoningslot] = true;
					resetFrame(c);
					return;
				} else if (!c.getItems().isStackable(itemID)) {

					for (int i = 0; i < amount; i++) {
						if (c.maxstore() <= c.totalstored()) {
							c.sendMessage("You cannot store anymore");
							return;
						}
						c.firstSlot(itemID);
						c.getPA().Frame34(2702, itemID, c.summoningslot, 1);
						// c.sendMessage("Non-Stackable "+c.summoningslot);
						c.getItems().deleteItem(itemID, 1);
						c.occupied[c.summoningslot] = true;
						c.storeditems[c.summoningslot] = itemID;
						c.amount[c.summoningslot] = amount;
						c.summoningslot += 1;
						c.totalstored += 1;
						c.getBank().resetTempItems();
						resetFrame(c);

					}
				}

			}
		}

	}

	public int pouchreq;
	// public boolean hasitem();
	private final static int SHARD = 18016; // 14015

	public static void resetFrame(Client c) {
		for (int k = 0; k < 28; k++) {
			if (c.storeditems[k] > 0) {
				c.getPA().Frame34(2702, c.storeditems[k], k, c.amount[k]);
			}

			if (c.storeditems[k] <= 0) {
				c.getPA().Frame34(2702, -1, k, c.amount[k]);
			}

		}
	}

	public int itemID;

	public int amount;

	public int fromSlot;

	private static final String[][] summoningPouchData = {
			// Summoning pouch making
			// Pouch id, pouch charm, item1, Shardamount, LVL, Spec scroll,
			// NPCID
			// TODO need to add NPCDEF's
			{ "Spirit wolf pouch", "Gold Charm", "Wolf bones", "7", "1",
					"Howl scroll", "6829" },
			{ "Dreadfowl pouch", "Gold Charm", "Raw chicken", "8", "4",
					"Dreadfowl strike scroll", "6825" },
			{ "Spirit spider pouch", "Gold Charm", "Spider carcass", "8", "10",
					"Egg spawn scroll", "6841" },
			{ "Thorny Snail pouch", "Gold Charm", "Thin snail", "9", "13",
					"Slime spray scroll", "6806" },
			{ "Granite Crab pouch", "Gold Charm", "Iron ore", "7", "16",
					"Stony shell scroll", "6796" },
			{ "Mosquito pouch", "Gold Charm", "Proboscis", "1", "17",
					"Pester scroll", "7331" },
			{ "Desert wyrm pouch", "Green Charm", "Bucket of sand", "45", "18",
					"Electric lash scroll", "6831" },
			{ "Spirit Scorpion pouch", "Crimson Charm", "Bronze claws", "57",
					"19", "Venom shot scroll", "6837" },
			{ "Spirit tz-kih pouch", "crimson charm", "Obsidian charm", "64",
					"22", "Fireball assault scroll", "7361" },
			{ "Albino rat pouch", "Blue Charm", "Raw rat meat", "75", "23",
					"Cheese feast scroll", "6847" },
			{ "Spirit kalphite pouch", "blue Charm", "potato cactus", "51",
					"25", "Sandstorm scroll", "6994" },
			{ "Compost mound pouch", "Green charm", "compost", "47", "28",
					"Generate compost scroll", "6871" },
			{ "Giant chinchompa pouch", "Blue Charm", "Chinchompa", "84", "29",
					"Explode scroll", "7353" },
			{ "Vampire bat pouch", "Crimson Charm", "Vampire dust", "81", "31",
					"Vampire touch scroll", "6835" },
			{ "Honey badger pouch", "Crimson Charm", "Honeycomb", "84", "32",
					"Insane ferocity scroll", "6845" },
			{ "Beaver pouch", "Green Charm", "Willow logs", "72", "33",
					"Multichop scroll", "6807" },
			{ "Void ravager pouch", "green Charm", "Ravager Charm", "74", "34",
					"Call to arms scroll", "7370" },
			{ "Void shifter pouch", "blue charm", "Shifter charm", "74", "34",
					"Call to arms scroll", "7367" },
			{ "void spinner pouch", "blue Charm", "spinner Charm", "74", "34",
					"Call to arms scroll", "7333" },
			{ "Void Torcher pouch", "blue Charm", "Torcher Charm", "74", "34",
					"Call to arms scroll", "7351" },
			{ "Bronze minotaur pouch", "Blue Charm", "Bronze bar", "102", "36",
					"Bronze bull rush scroll", "6853" },
			{ "Bull ant pouch", "gold Charm", "Marigolds", "11", "40",
					"Unburden scroll", "6867" },
			{ "Macaw pouch", "green Charm", "Clean guam", "78", "41",
					"Herbcall scroll", "6851" },

			{ "Evil turnip pouch", "crimson Charm", "Carved turnip", "104",
					"42", "Evil flames scroll", "6833" },

			{ "Iron minotaur pouch", "Blue Charm", "Iron bar", "125", "46",
					"Iron bull rush scroll", "6855" },
			{ "Pyrelord pouch", "Crimson Charm", "Tinderbox", "111", "46",
					"Immense heat scroll", "7377" },
			{ "Magpie pouch", "green Charm", "Gold ring", "88", "47",
					"Thieving fingers scroll", "6824" },

			{ "Bloated leech pouch", "Crimson Charm", "Raw beef", "117", "49",
					"Blood drain scroll", "6843" },
			{ "Spirit terrorbird pouch", "Gold Charm", "Raw bird meat", "12",
					"52", "Tireless run scroll", "3596" },
			{ "Abyssal parasite pouch", "green Charm", "Abyssal charm", "106",
					"54", "Abyssal drain scroll", "6818" },
			{ "Spirit jelly pouch", "blue Charm", "Jug of water", "151", "55",
					"Dissolve scroll", "6922" },
			{ "Steel minotaur pouch", "blue Charm", "steel bar", "141", "56",
					"Fish rain scroll", "6857" },
			{ "Ibis pouch", "green Charm", "Harpoon", "109", "56",
					"Steel bull rush scroll", "6991" },
			{ "Spirit Graahk pouch", "blue Charm", "graahk fur", "154", "57",
					"Ambush scroll", "3588" },
			{ "Spirit Kyatt pouch", "blue Charm", "Kyatt fur", "153", "57",
					"Rending scroll", "7365" },
			{ "Spirit larupia pouch", "blue Charm", "larupia fur", "155", "57",
					"Goad scroll", "7337" },
			{ "Karamthulhu overlord pouch", "blue Charm", "Empty fishbowl",
					"144", "58", "Doomsphere scroll", "6809" },
			{ "Smoke devil pouch", "Crimson Charm", "Goat horn dust", "141",
					"61", "Dust cloud scroll", "6865" },
			{ "Abyssal lurker", "green Charm", "Abyssal charm", "119", "62",
					"Abyssal stealth scroll", "6820" },
			{ "Spirit cobra pouch", "Crimson Charm", "Snake hide", "116", "63",
					"Ophidian incubation scroll", "6802" },
			{ "Stranger plant pouch", "Crimson Charm", "Bagged plant", "128",
					"64", "Poisonous blast scroll", "6827" },
			{ "Mithril minotaur pouch", "Blue Charm", "Mithril bar", "152",
					"66", "Mithril bull rush scroll", "6859" },
			{ "Barker toad pouch", "Gold Charm", "Swamp toad", "11", "66",
					"Toad bark scroll", "6889" },
			{ "War tortoise pouch", "Gold Charm", "Tortoise shell", "1", "67",
					"Testudo scroll", "6815" },
			{ "Bunyip pouch", "Green Charm", "Raw shark", "110", "68",
					"Swallow whole scroll", "6813" },
			{ "Fruit bat pouch", "Green Charm", "Banana", "130", "69",
					"Fruitfall scroll", "6817" },
			{ "Ravenous Locust pouch", "Crimson Charm", "Pot of Flour", "79",
					"70", "Famine scroll", "7372" },
			{ "Arctic bear pouch", "Gold Charm", "Polar kebbit fur", "14",
					"71", "Arctic blast scroll", "6839" },
			{ "Phoenix pouch", "Crimson Charm", "Phoenix Quill", "165", "72",
					"Phoenix unknown scroll", "1911" },
			{ "Obsidian Golem pouch", "Blue Charm", "Obsidian Charm", "195",
					"73", "Volcanic strength scroll", "7345" },
			{ "Granite lobster pouch", "Crimson Charm", "Granite (500g)",
					"166", "74", "Crushing claw scroll", "6849" },
			{ "Praying mantis pouch", "Crimson Charm", "Flowers", "168", "75",
					"Mantis strike scroll", "6798" },
			{ "Adamant minotaur pouch", "Blue Charm", "Adamant Bar", "144",
					"76", "Inferno scroll", "6861" },
			{ "Forge Regent pouch", "Green Charm", "Ruby harvest", "141", "76",
					"Adamant bull rush scroll", "7335" },
			{ "Talon Beast pouch", "Crimson Charm", "Talon Beast charm", "174",
					"77", "Deadly claw scroll", "7347" },
			{ "Giant ent pouch", "Green Charm", "Willow branch", "124", "78",
					"Acorn missile scroll", "6800" },
			{ "Fire titan pouch", "Blue Charm", "Fire talisman", "198", "79",
					"Titan's constitution scroll", "7355" },
			{ "Ice titan pouch", "Blue Charm", "Water talisman", "198", "79",
					"Titan's constitution scroll", "7359" },
			{ "Moss titan pouch", "Blue Charm", "Earth talisman", "202", "79",
					"Titan's constitution scroll", "7357" },
			{ "Hydra pouch", "Green Charm", "Water orb", "128", "80",
					"Regrowth scroll", "6811" },
			{ "Spirit dagannoth", "Crimson Charm", "Dagannoth hide", "122",
					"83", "Spike shot scroll", "6804" },
			{ "Lava titan pouch", "Blue Charm", "Obsidian Charm", "219", "83",
					"Ebon thunder scroll", "7341" },
			{ "Swamp titan pouch", "Blue Charm", "Swamp lizard", "150", "85",
					"Swamp plague scroll", "7329" },
			{ "Rune minotaur pouch", "Blue Charm", "Rune bar", "111", "86",
					"Rune bull rush scroll", "6863" },
			{ "Unicorn stallion pouch", "green Charm", "Unicorn Horn", "140",
					"88", "Healing aura scroll", "3592" },
			{ "Geyser titan pouch", "blue Charm", "Water talisman", "222",
					"89", "Boil scroll", "7339" },
			{ "Wolpertinger pouch", "crimson Charm", "Raw rabbit", "203", "92",
					"Magic focus scroll", "3593" },
			{ "Abyssal titan pouch", "green Charm", "Abyssal charm", "113",
					"93", "Essence shipment scroll", "7349" },
			{ "Iron titan pouch", "crimson Charm", "Iron platebody", "198",
					"95", "Iron within scroll", "7375" },
			{ "Pack yak pouch", "Crimson Charm", "Yak-hide", "211", "96",
					"Winter storage scroll", "3594" },
			{ "Steel titan pouch", "Blue Charm", "Steel platebody", "178",
					"99", "Steel of legends scroll", "3591" },

	};

	public int pouch = 12155;

	public int req;

	public Summoning(Client c) {
		this.c = c;
	}

	public void ItemonItem(int itemUsed, int useWith) {
	}

	public void makeSummoningScroll(Client c, int pouchUsed) {

		for (int i = 0; i < summoningPouchData.length; i++) {
			if (pouchUsed == c.getItems().getItemId(summoningPouchData[i][0])) {
				if (c.getItems().playerHasItem(
						c.getItems().getItemId(summoningPouchData[i][0]), 1)
						&& c.playerLevel[21] >= Integer
								.parseInt(summoningPouchData[i][4])) {
					c.getItems()
							.deleteItem(
									c.getItems().getItemId(
											summoningPouchData[i][0]), 1);
					c.getItems()
							.addItem(
									c.getItems().getItemId(
											summoningPouchData[i][5]), 1);
					c.getPA().addSkillXP(
							Integer.parseInt(summoningPouchData[i][4]) * 900,
							21);
					break;
				} else {
					c.sendMessage("You need a higher summoning level to make this scroll");
					break;
				}
			}
		}
	}

	public void removeItems() {
		for (int i = 0; i < 29; i += 1) {
			if (NPCHandler.npcs[c.summoningnpcid] != null) {
				Server.itemHandler.createGroundItem(c, c.storeditems[i],
						NPCHandler.npcs[c.summoningnpcid].absX,
						NPCHandler.npcs[c.summoningnpcid].absY, 1, c.playerId);
				c.storeditems[i] = -1;
				// c.occupied[i] = false;
			}
		}
	}

	public void store() // redone by gabbe- THERE SHOULD BE NO DUPES!!!
	{
		if (c.InDung()) {
			c.sendMessage("You can't do this in dung idiot!");
			return;
		}
		if (c.inTrade) {
			c.sendMessage("You can't do this in a Trade!");
			c.getTradeAndDuel().declineTrade();
			c.getTradeAndDuel().declineDuel();
			return;
		}
		if (c.tradeStatus == 1) {
			c.getTradeAndDuel().declineTrade();
			return;
		}
		if (c.duelStatus == 1) {
			c.getTradeAndDuel().declineDuel();
			return;
		}
		/*
		 * 
		 * c.getPA().sendFrame126("Summoning BoB", 7421); for (int k = 0; k <
		 * 29; k++) { if(c.storeditems[k] > 0) { c.getPA().Frame34(7423,
		 * c.storeditems[k], k, 1); }
		 * 
		 * 
		 * if(c.storeditems[k] <= 0) { c.getPA().Frame34(7423, -1, k, 1); }
		 * 
		 * }
		 * 
		 * 
		 * c.isBanking = true; c.storing = true; c.getItems().resetItems(5064);
		 * 
		 * c.getItems().rearrangeBank(); c.getItems().resetBank();
		 * c.getItems().resetTempItems(); //c.getOutStream().createFrame(248);
		 * 
		 * //c.getOutStream().writeWordA(4465);
		 * //c.getOutStream().writeWord(5063);
		 * //c.getOutStream().writeWord(10600); //c.getPA().sendFrame87(286, 0);
		 * 
		 * c.flushOutStream();
		 * 
		 * 
		 * 
		 * 
		 * 
		 * //c.ResetKeepItems();
		 */
		openInterface(c);
	}

	public void SummonNewNPC(int npcID) {
		if (c.InDung()) {
			c.sendMessage("You can't do this here!");
			return;
		}
		if (c.isInPrivCon()) {
			c.sendMessage("You can't do this here!");
			return;
		}
		int maxhit = 0;
		int attack = 0;
		int defence = 0;
		// c.getPA().sendFrame75(c.hasFollower, 17027);

		switch (npcID) {
		case 6830:
			c.summonTime = -1;
			maxhit = 4;
			attack = 10;
			defence = 80;

			break;

		case 6826:
			c.summonTime = -1;
			maxhit = 6;
			attack = 10;
			defence = 80;
			break;

		case 6842:
			c.summonTime = -1;
			maxhit = 6;
			attack = 10;
			defence = 80;
			break;

		case 6807:
			c.summonTime = -1;
			maxhit = 5;
			attack = 20;
			defence = 80;
			c.maxstore = 3;
			c.summonTime = 1200;
			break;

		case 6797:
			c.summonTime = -1;
			maxhit = 8;
			attack = 20;
			defence = 80;
			break;

		case 7332:
			c.summonTime = -1;
			maxhit = 8;
			attack = 20;
			defence = 80;
			break;

		case 6832:
			c.summonTime = -1;
			maxhit = 8;
			attack = 20;
			defence = 80;
			break;

		case 6838:
			c.summonTime = -1;
			maxhit = 8;
			attack = 20;
			defence = 80;
			break;

		case 7362:
			c.summonTime = -1;
			maxhit = 8;
			attack = 20;
			defence = 80;
			break;

		case 6848:
			c.summonTime = -1;
			maxhit = 8;
			attack = 20;
			defence = 80;
			break;

		case 6995:
			c.summonTime = -1;
			maxhit = 10;
			attack = 20;
			defence = 80;
			break;

		case 6872:
			c.summonTime = -1;
			maxhit = 10;
			attack = 20;
			defence = 80;
			break;

		case 7354:
			c.summonTime = -1;
			maxhit = 11;
			attack = 20;
			defence = 80;
			break;

		case 6836:
			c.summonTime = -1;
			maxhit = 12;
			attack = 20;
			defence = 80;
			break;

		case 6846:
			c.summonTime = -1;
			maxhit = 14;
			attack = 40;
			defence = 80;
			break;

		case 6808:
			c.summonTime = -1;
			maxhit = 12;
			attack = 40;
			defence = 80;
			;
			break;

		case 7371:
			c.summonTime = -1;
		case 7369:
			c.summonTime = -1;
		case 7368:
			c.summonTime = -1;
		case 7370:
			c.summonTime = -1;
		case 7352:
			c.summonTime = -1;
			maxhit = 11;
			attack = 40;
			defence = 80;
			break;

		case 6854:
			c.summonTime = -1;
		case 68:
			c.summonTime = -1;
			maxhit = 12;
			attack = 40;
			defence = 80;
			break;

		case 6868:
			c.summonTime = -1;
			maxhit = 12;
			attack = 40;
			defence = 80;
			c.maxstore = 6;
			c.summonTime = 2400;
			break;

		case 6852:
			c.summonTime = -1;
			maxhit = 8;
			attack = 40;
			defence = 80;
			break;
		case 6834:
			c.summonTime = -1;
			maxhit = 14;
			attack = 40;
			defence = 80;
			break;

		case 6856:
			c.summonTime = -1;
			maxhit = 15;
			attack = 40;
			defence = 80;
			break;

		case 7378:
			c.summonTime = -1;
			maxhit = 14;
			attack = 40;
			defence = 80;
			break;

		case 6824:
			c.summonTime = -1;
			maxhit = 13;
			attack = 40;
			defence = 80;
			break;

		case 6844:
			c.summonTime = -1;
			maxhit = 12;
			attack = 40;
			defence = 80;
			break;

		case 6795:
			c.summonTime = -1;
			c.maxstore = 12;
			maxhit = 11;
			attack = 60;
			defence = 80;
			c.summonTime = 3000;
			break;

		case 6819:
			c.summonTime = -1;
			maxhit = 13;
			attack = 60;
			defence = 80;
			break;
		case 6993:
			c.summonTime = -1;
			maxhit = 15;
			attack = 60;
			defence = 80;
			break;

		case 6858:
			c.summonTime = -1;
			maxhit = 11;
			attack = 60;
			defence = 80;
			break;

		case 6991:
			c.summonTime = -1;
			maxhit = 11;
			attack = 60;
			defence = 80;
			break;

		case 7364:
			c.summonTime = -1;
		case 7366:
			c.summonTime = -1;
		case 7338:
			c.summonTime = -1;
			maxhit = 20;
			attack = 60;
			defence = 80;
			break;

		case 6810:
			c.summonTime = -1;
			maxhit = 11;
			attack = 60;
			defence = 80;
			break;

		case 6821:
			c.summonTime = -1;
			maxhit = 11;
			attack = 60;
			defence = 80;
			break;

		case 6803:
			c.summonTime = -1;
			maxhit = 14;
			attack = 60;
			defence = 80;
			break;

		case 6828:
			c.summonTime = -1;
			maxhit = 18;
			attack = 60;
			defence = 80;
			break;

		case 6860:
			c.summonTime = -1;
			maxhit = 20;
			attack = 60;
			defence = 80;
			break;

		case 6890:
			c.summonTime = -1;
			maxhit = 20;
			attack = 60;
			defence = 80;
			break;

		case 6816:
			c.maxstore = 18;
			c.summonTime = 3800; // kills the npc - 60*2 = 120 = 1 minute
			maxhit = 21;
			attack = 60;
			defence = 80;
			break;

		case 6814:
			c.summonTime = -1;
			maxhit = 17;
			attack = 60;
			defence = 80;
			break;

		case 7372:
			c.summonTime = -1;
		case 7373:
			c.summonTime = -1;
		case 7374:
			c.summonTime = -1;
			maxhit = 11;
			attack = 60;
			defence = 80;
			break;

		case 6840:
			c.summonTime = -1;
			pouchreq = 71;
			break;

		case 6817:
			c.summonTime = -1;
			maxhit = 11;
			attack = 60;
			defence = 80;
			break;
		case 8576:
			c.summonTime = -1;
			pouchreq = 99;
			break;

		case 7346:
			c.summonTime = -1;
			maxhit = 25;
			attack = 80;
			defence = 80;
			break;

		case 6799:
			c.summonTime = -1;
			maxhit = 11;
			attack = 60;
			defence = 80;
			break;

		case 6850:
			c.summonTime = -1;
			maxhit = 11;
			attack = 60;
			defence = 80;
			break;

		case 6862:
			c.summonTime = -1;
			maxhit = 22;
			attack = 60;
			defence = 80;
			break;

		case 7336:
			c.summonTime = -1;
			maxhit = 24;
			attack = 60;
			defence = 80;
			break;

		case 6801:
			maxhit = 11;
			attack = 60;
			defence = 80;
			c.summonTime = 3040;
			break;

		case 7356:
		case 7358:
		case 7360:
			maxhit = 26;
			attack = 60;
			defence = 80;
			c.summonTime = 4440;
			break;

		case 6812:
			c.summonTime = -1;
			maxhit = 28;
			attack = 60;
			defence = 80;
			break;

		case 6805:
		case 7342:
			maxhit = 30;
			attack = 60;
			defence = 80;
			c.summonTime = 4000;
			break;

		case 7330:
			maxhit = 31;
			attack = 60;
			defence = 80;
			c.summonTime = 4100;
			break;
		case 6864:
			maxhit = 32;
			attack = 60;
			defence = 80;
			c.summonTime = 4100;
			break;
		case 6823:
			maxhit = 33;
			attack = 60;
			defence = 80;
			c.summonTime = 2400;
			break;
		case 7340:
			maxhit = 34;
			attack = 60;
			defence = 80;
			c.summonTime = 3840;
			break;

		case 6870:
			maxhit = 35;
			attack = 60;
			defence = 80;
			c.summonTime = 3240;
			break;

		case 7350:
			maxhit = 36;
			attack = 60;
			defence = 80;
			c.summonTime = 4200;
			break;

		case 7376:
			maxhit = 37;
			attack = 60;
			defence = 80;
			break;
		case 6874:
			c.maxstore = 28;
			c.summonTime = 4800;
			maxhit = 38;
			attack = 60;
			defence = 80;
			break;
		case 7344:
			maxhit = 39;
			attack = 90;
			defence = 80;
			c.summonTime = 6000;
			break;
		}
		switch (npcID) {
		case 6830:
			c.summonTime = -1;
			pouchreq = 0;
			break;

		case 6826:
			c.summonTime = -1;
			pouchreq = 4;
			break;

		case 6842:
			c.summonTime = -1;
			pouchreq = 10;
			break;

		case 6807:
			c.summonTime = -1;
			pouchreq = 13;
			break;

		case 6797:
			c.summonTime = -1;
			pouchreq = 16;
			break;

		case 7332:
			c.summonTime = -1;
			pouchreq = 17;
			break;

		case 6832:
			c.summonTime = -1;
			pouchreq = 18;
			break;

		case 6838:
			c.summonTime = -1;
			pouchreq = 19;
			break;

		case 7362:
			c.summonTime = -1;
			pouchreq = 22;
			break;

		case 6848:
			c.summonTime = -1;
			pouchreq = 23;
			break;

		case 6995:
			c.summonTime = -1;
			pouchreq = 25;
			break;

		case 6872:
			c.summonTime = -1;
			pouchreq = 28;
			break;

		case 7354:
			c.summonTime = -1;
			pouchreq = 29;
			break;

		case 6836:
			c.summonTime = -1;
			pouchreq = 31;
			break;

		case 6846:
			c.summonTime = -1;
			pouchreq = 32;
			break;

		case 6808:
			c.summonTime = -1;
			pouchreq = 33;
			break;

		case 7371:
			c.summonTime = -1;
		case 7369:
			c.summonTime = -1;
		case 7368:
			c.summonTime = -1;
		case 7370:
			c.summonTime = -1;
		case 7352:
			c.summonTime = -1;
			pouchreq = 34;
			break;

		case 6854:
			c.summonTime = -1;
		case 68:
			c.summonTime = -1;
			pouchreq = 36;
			break;

		case 6868:
			c.summonTime = -1;
			pouchreq = 40;
			break;

		case 6852:
			c.summonTime = -1;
			pouchreq = 41;
			break;
		case 6834:
			c.summonTime = -1;
			pouchreq = 42;
			break;

		case 6856:
			c.summonTime = -1;
			pouchreq = 46;
			break;

		case 7378:
			c.summonTime = -1;
			pouchreq = 46;
			break;

		case 6824:
			c.summonTime = -1;
			pouchreq = 47;
			break;

		case 6844:
			c.summonTime = -1;
			pouchreq = 49;
			break;

		case 6795:
			c.summonTime = -1;
			pouchreq = 52;
			break;

		case 6819:
			c.summonTime = -1;
			pouchreq = 54;
			break;
		case 6993:
			c.summonTime = -1;
			pouchreq = 55;
			break;

		case 6858:
			c.summonTime = -1;
			pouchreq = 56;
			break;

		case 6991:
			c.summonTime = -1;
			pouchreq = 56;
			break;

		case 7364:
			c.summonTime = -1;
		case 7366:
			c.summonTime = -1;
		case 7338:
			c.summonTime = -1;
			pouchreq = 57;
			break;

		case 6810:
			c.summonTime = -1;
			pouchreq = 58;
			break;

		case 6866:
			c.summonTime = -1;
			pouchreq = 99;
			break;

		case 6821:
			c.summonTime = -1;
			pouchreq = 62;
			break;

		case 6803:
			c.summonTime = -1;
			pouchreq = 63;
			break;

		case 6828:
			c.summonTime = -1;
			pouchreq = 64;
			break;

		case 6860:
			c.summonTime = -1;
			pouchreq = 66;
			break;

		case 6890:
			c.summonTime = -1;
			pouchreq = 66;
			break;

		case 6816:
			c.summonTime = -1;
			pouchreq = 67;
			break;

		case 6814:
			c.summonTime = -1;
			pouchreq = 68;
			c.summonTime = 2000;
			break;

		case 7372:
			c.summonTime = -1;
		case 7373:
			c.summonTime = -1;
		case 7374:
			c.summonTime = -1;
			pouchreq = 70;
			break;

		case 6840:
			c.summonTime = -1;
			pouchreq = 71;
			break;

		case 6817:
			c.summonTime = -1;
			pouchreq = 69;
			break;
		case 8576:
			c.summonTime = -1;
			pouchreq = 99;
			break;

		case 7346:
			c.summonTime = -1;
			pouchreq = 73;
			break;

		case 6799:
			c.summonTime = -1;
			pouchreq = 75;
			break;

		case 6850:
			c.summonTime = -1;
			pouchreq = 74;
			break;

		case 6862:
			c.summonTime = -1;
			pouchreq = 76;
			break;

		case 7336:
			c.summonTime = -1;
			pouchreq = 76;
			break;

		case 6801:
			c.summonTime = -1;
			pouchreq = 78;
			break;

		case 7356:
			c.summonTime = -1;
		case 7358:
			c.summonTime = -1;
		case 7360:
			c.summonTime = -1;
			pouchreq = 79;
			break;

		case 6812:
			c.summonTime = -1;
			pouchreq = 80;
			break;

		case 6805:
			c.summonTime = -1;
		case 7342:
			c.summonTime = -1;
			pouchreq = 83;
			break;

		case 7330:
			c.summonTime = -1;
			pouchreq = 85;
			break;
		case 6864:
			c.summonTime = -1;
			pouchreq = 86;
			break;
		case 6823:
			c.summonTime = -1;
			pouchreq = 88;
			break;
		case 7340:
			c.summonTime = -1;
			pouchreq = 89;
			break;

		case 6870:
			c.summonTime = -1;
			pouchreq = 92;
			break;

		case 7350:
			c.summonTime = -1;
			pouchreq = 93;
			break;

		case 7376:
			c.summonTime = -1;
			pouchreq = 95;
			break;
		case 6874:
			c.summonTime = -1;
			pouchreq = 96;
			break;
		case 7344:
			c.summonTime = -1;
			pouchreq = 99;
			break;
		}
		if (c.playerLevel[21] >= pouchreq) {
			Server.npcHandler.Summon(c, npcID, c.absX, c.absY - 2,
					c.heightLevel, 0, 100, maxhit, false, attack, defence);
			if (npcID == 6830 && !c.task1[6]) {
				c.sendMessage("You've completed the task: Summon a Spirit Wolf!");
				c.task1[6] = true;
				c.TPoints += 1;
				if (c.TPoints == 1) {
					c.sendMessage("You've received a Task Point! You now have "
							+ c.TPoints + " point!");
				} else {
					c.sendMessage("You've received a Task Point! You now have a total of "
							+ c.TPoints + " points!");
				}
				c.achievementInterface("Summon a Spirit Wolf!");
			}
			c.sumfam += 1;
			if (c.sumfam >= 150 && !c.task3[5]) {
				GabbesAchievements.handleEliteTask(c, 5, 3,
						"Summon 150 Familiars!");
			}
			c.getItems().deleteItem(c.s, 1);
			for (int i = 0; i < NPCHandler.maxNPCs; i++) {
				if (NPCHandler.npcs[i] != null) {
					c.npcslot = NPCHandler.npcs[i].npcId;
				}
				// c.getPA().sendFrame126("   " + c.playerLevel[21] + "/"+
				// c.getLevelForXP(c.playerXP[21]), 17025);
				// c.getPA().sendFrame126("" + c.summonTime / 120 +
				// ".00 Min",17021);
				if (c.hasFollower == 6870 || c.hasFollower == 6814) {
					// c.startEvent5(c);
				}
				if (c.summonTime == -1) {
					c.summonTime = 0;
				}
			}
		} else {
			c.sendMessage("You need " + pouchreq
					+ " Summoning to summon this monster.");
		}
	}

	/**/
	/***
	 * Fixes the deleteing pouch problem, when a new npc is summoned because the
	 * player has teleported! /
	 **/

	public void summonNulledNPC(int npcID) {
		if (c.InDung()) {
			c.sendMessage("You can't do this in dung!");
			return;
		}
		int maxhit = 0;
		int attack = 0;
		int defence = 0;
		// c.getPA().sendFrame75(c.hasFollower, 17027);

		switch (npcID) {
		case 6830:
			c.summonTime = -1;
			maxhit = 4;
			attack = 10;
			defence = 80;

			break;

		case 6826:
			c.summonTime = -1;
			maxhit = 6;
			attack = 10;
			defence = 80;
			break;

		case 6842:
			c.summonTime = -1;
			maxhit = 6;
			attack = 10;
			defence = 80;
			break;

		case 6807:
			c.summonTime = -1;
			maxhit = 5;
			attack = 20;
			defence = 80;
			c.maxstore = 3;
			c.summonTime = 1200;
			break;

		case 6797:
			c.summonTime = -1;
			maxhit = 8;
			attack = 20;
			defence = 80;
			break;

		case 7332:
			c.summonTime = -1;
			maxhit = 8;
			attack = 20;
			defence = 80;
			break;

		case 6832:
			c.summonTime = -1;
			maxhit = 8;
			attack = 20;
			defence = 80;
			break;

		case 6838:
			c.summonTime = -1;
			maxhit = 8;
			attack = 20;
			defence = 80;
			break;

		case 7362:
			c.summonTime = -1;
			maxhit = 8;
			attack = 20;
			defence = 80;
			break;

		case 6848:
			c.summonTime = -1;
			maxhit = 8;
			attack = 20;
			defence = 80;
			break;

		case 6995:
			c.summonTime = -1;
			maxhit = 10;
			attack = 20;
			defence = 80;
			break;

		case 6872:
			c.summonTime = -1;
			maxhit = 10;
			attack = 20;
			defence = 80;
			break;

		case 7354:
			c.summonTime = -1;
			maxhit = 11;
			attack = 20;
			defence = 80;
			break;

		case 6836:
			c.summonTime = -1;
			maxhit = 12;
			attack = 20;
			defence = 80;
			break;

		case 6846:
			c.summonTime = -1;
			maxhit = 14;
			attack = 40;
			defence = 80;
			break;

		case 6808:
			c.summonTime = -1;
			maxhit = 12;
			attack = 40;
			defence = 80;
			;
			break;

		case 7371:
			c.summonTime = -1;
		case 7369:
			c.summonTime = -1;
		case 7368:
			c.summonTime = -1;
		case 7370:
			c.summonTime = -1;
		case 7352:
			c.summonTime = -1;
			maxhit = 11;
			attack = 40;
			defence = 80;
			break;

		case 6854:
			c.summonTime = -1;
		case 68:
			c.summonTime = -1;
			maxhit = 12;
			attack = 40;
			defence = 80;
			break;

		case 6868:
			c.summonTime = -1;
			maxhit = 12;
			attack = 40;
			defence = 80;
			c.maxstore = 6;
			c.summonTime = 2400;
			break;

		case 6852:
			c.summonTime = -1;
			maxhit = 8;
			attack = 40;
			defence = 80;
			break;
		case 6834:
			c.summonTime = -1;
			maxhit = 14;
			attack = 40;
			defence = 80;
			break;

		case 6856:
			c.summonTime = -1;
			maxhit = 15;
			attack = 40;
			defence = 80;
			break;

		case 7378:
			c.summonTime = -1;
			maxhit = 14;
			attack = 40;
			defence = 80;
			break;

		case 6824:
			c.summonTime = -1;
			maxhit = 13;
			attack = 40;
			defence = 80;
			break;

		case 6844:
			c.summonTime = -1;
			maxhit = 12;
			attack = 40;
			defence = 80;
			break;

		case 6795:
			c.summonTime = -1;
			c.maxstore = 12;
			maxhit = 11;
			attack = 60;
			defence = 80;
			c.summonTime = 3000;
			break;

		case 6819:
			c.summonTime = -1;
			maxhit = 13;
			attack = 60;
			defence = 80;
			break;
		case 6993:
			c.summonTime = -1;
			maxhit = 15;
			attack = 60;
			defence = 80;
			break;

		case 6858:
			c.summonTime = -1;
			maxhit = 11;
			attack = 60;
			defence = 80;
			break;

		case 6991:
			c.summonTime = -1;
			maxhit = 11;
			attack = 60;
			defence = 80;
			break;

		case 7364:
			c.summonTime = -1;
		case 7366:
			c.summonTime = -1;
		case 7338:
			c.summonTime = -1;
			maxhit = 20;
			attack = 60;
			defence = 80;
			break;

		case 6810:
			c.summonTime = -1;
			maxhit = 11;
			attack = 60;
			defence = 80;
			break;

		case 6821:
			c.summonTime = -1;
			maxhit = 11;
			attack = 60;
			defence = 80;
			break;

		case 6803:
			c.summonTime = -1;
			maxhit = 14;
			attack = 60;
			defence = 80;
			break;

		case 6828:
			c.summonTime = -1;
			maxhit = 18;
			attack = 60;
			defence = 80;
			break;

		case 6860:
			c.summonTime = -1;
			maxhit = 20;
			attack = 60;
			defence = 80;
			break;

		case 6890:
			c.summonTime = -1;
			maxhit = 20;
			attack = 60;
			defence = 80;
			break;

		case 6816:
			c.summonTime = -1;
			c.maxstore = 18;
			c.summonTime = 3800; // kills the npc - 60*2 = 120 = 1 minute
			maxhit = 21;
			attack = 60;
			defence = 80;
			break;

		case 6814:
			c.summonTime = -1;
			maxhit = 17;
			attack = 60;
			defence = 80;
			break;

		case 7372:
			c.summonTime = -1;
		case 7373:
			c.summonTime = -1;
		case 7374:
			c.summonTime = -1;
			maxhit = 11;
			attack = 60;
			defence = 80;
			break;

		case 6840:
			c.summonTime = -1;
			pouchreq = 71;
			break;

		case 6817:
			c.summonTime = -1;
			maxhit = 11;
			attack = 60;
			defence = 80;
			break;
		case 8576:
			c.summonTime = -1;
			pouchreq = 99;
			break;

		case 7346:
			c.summonTime = -1;
			maxhit = 25;
			attack = 80;
			defence = 80;
			break;

		case 6799:
			c.summonTime = -1;
			maxhit = 11;
			attack = 60;
			defence = 80;
			break;

		case 6850:
			c.summonTime = -1;
			maxhit = 11;
			attack = 60;
			defence = 80;
			break;

		case 6862:
			c.summonTime = -1;
			maxhit = 22;
			attack = 60;
			defence = 80;
			break;

		case 7336:
			c.summonTime = -1;
			maxhit = 24;
			attack = 60;
			defence = 80;
			break;

		case 6801:
			maxhit = 11;
			attack = 60;
			defence = 80;
			c.summonTime = 3040;
			break;

		case 7356:
		case 7358:
		case 7360:
			maxhit = 26;
			attack = 60;
			defence = 80;
			c.summonTime = 4440;
			break;

		case 6812:
			c.summonTime = -1;
			maxhit = 28;
			attack = 60;
			defence = 80;
			break;

		case 6805:
		case 7342:
			maxhit = 30;
			attack = 60;
			defence = 80;
			c.summonTime = 4000;
			break;

		case 7330:
			maxhit = 31;
			attack = 60;
			defence = 80;
			c.summonTime = 4100;
			break;
		case 6864:
			maxhit = 32;
			attack = 60;
			defence = 80;
			c.summonTime = 4100;
			break;
		case 6823:
			maxhit = 33;
			attack = 60;
			defence = 80;
			c.summonTime = 2400;
			break;
		case 7340:
			maxhit = 34;
			attack = 60;
			defence = 80;
			c.summonTime = 3840;
			break;

		case 6870:
			maxhit = 35;
			attack = 60;
			defence = 80;
			c.summonTime = 3240;
			break;

		case 7350:
			maxhit = 36;
			attack = 60;
			defence = 80;
			c.summonTime = 4200;
			break;

		case 7376:
			maxhit = 37;
			attack = 60;
			defence = 80;
			break;
		case 6874:
			c.maxstore = 28;
			c.summonTime = 4800;
			maxhit = 38;
			attack = 60;
			defence = 80;
			break;
		case 7344:
			maxhit = 39;
			attack = 90;
			defence = 80;
			c.summonTime = 6000;
			break;
		}
		switch (npcID) {
		case 6830:
			c.summonTime = -1;
			pouchreq = 0;
			break;

		case 6826:
			c.summonTime = -1;
			pouchreq = 4;
			break;

		case 6842:
			c.summonTime = -1;
			pouchreq = 10;
			break;

		case 6807:
			c.summonTime = -1;
			pouchreq = 13;
			break;

		case 6797:
			c.summonTime = -1;
			pouchreq = 16;
			break;

		case 7332:
			c.summonTime = -1;
			pouchreq = 17;
			break;

		case 6832:
			c.summonTime = -1;
			pouchreq = 18;
			break;

		case 6838:
			c.summonTime = -1;
			pouchreq = 19;
			break;

		case 7362:
			c.summonTime = -1;
			pouchreq = 22;
			break;

		case 6848:
			c.summonTime = -1;
			pouchreq = 23;
			break;

		case 6995:
			c.summonTime = -1;
			pouchreq = 25;
			break;

		case 6872:
			c.summonTime = -1;
			pouchreq = 28;
			break;

		case 7354:
			c.summonTime = -1;
			pouchreq = 29;
			break;

		case 6836:
			c.summonTime = -1;
			pouchreq = 31;
			break;

		case 6846:
			c.summonTime = -1;
			pouchreq = 32;
			break;

		case 6808:
			c.summonTime = -1;
			pouchreq = 33;
			break;

		case 7371:
			c.summonTime = -1;
		case 7369:
			c.summonTime = -1;
		case 7368:
			c.summonTime = -1;
		case 7370:
			c.summonTime = -1;
		case 7352:
			c.summonTime = -1;
			pouchreq = 34;
			break;

		case 6854:
			c.summonTime = -1;
		case 68:
			c.summonTime = -1;
			pouchreq = 36;
			break;

		case 6868:
			c.summonTime = -1;
			pouchreq = 40;
			break;

		case 6852:
			c.summonTime = -1;
			pouchreq = 41;
			break;
		case 6834:
			c.summonTime = -1;
			pouchreq = 42;
			break;

		case 6856:
			c.summonTime = -1;
			pouchreq = 46;
			break;

		case 7378:
			c.summonTime = -1;
			pouchreq = 46;
			break;

		case 6824:
			c.summonTime = -1;
			pouchreq = 47;
			break;

		case 6844:
			c.summonTime = -1;
			pouchreq = 49;
			break;

		case 6795:
			c.summonTime = -1;
			pouchreq = 52;
			break;

		case 6819:
			c.summonTime = -1;
			pouchreq = 54;
			break;
		case 6993:
			c.summonTime = -1;
			pouchreq = 55;
			break;

		case 6858:
			c.summonTime = -1;
			pouchreq = 56;
			break;

		case 6991:
			c.summonTime = -1;
			pouchreq = 56;
			break;

		case 7364:
			c.summonTime = -1;
		case 7366:
			c.summonTime = -1;
		case 7338:
			c.summonTime = -1;
			pouchreq = 57;
			break;

		case 6810:
			c.summonTime = -1;
			pouchreq = 58;
			break;

		case 6866:
			c.summonTime = -1;
			pouchreq = 99;
			break;

		case 6821:
			c.summonTime = -1;
			pouchreq = 62;
			break;

		case 6803:
			c.summonTime = -1;
			pouchreq = 63;
			break;

		case 6828:
			c.summonTime = -1;
			pouchreq = 64;
			break;

		case 6860:
			c.summonTime = -1;
			pouchreq = 66;
			break;

		case 6890:
			c.summonTime = -1;
			pouchreq = 66;
			break;

		case 6816:
			c.summonTime = 3800;
			pouchreq = 67;
			break;

		case 6814:
			pouchreq = 68;
			c.summonTime = 2000;
			break;

		case 7372:
			c.summonTime = -1;
		case 7373:
			c.summonTime = -1;
		case 7374:
			c.summonTime = -1;
			pouchreq = 70;
			break;

		case 6840:
			c.summonTime = -1;
			pouchreq = 71;
			break;

		case 6817:
			c.summonTime = -1;
			pouchreq = 69;
			break;
		case 8576:
			c.summonTime = -1;
			pouchreq = 99;
			break;

		case 7346:
			c.summonTime = -1;
			pouchreq = 73;
			break;

		case 6799:
			c.summonTime = -1;
			pouchreq = 75;
			break;

		case 6850:
			c.summonTime = -1;
			pouchreq = 74;
			break;

		case 6862:
			c.summonTime = -1;
			pouchreq = 76;
			break;

		case 7336:
			c.summonTime = -1;
			pouchreq = 76;
			break;

		case 6801:
			c.summonTime = -1;
			pouchreq = 78;
			break;

		case 7356:
			c.summonTime = -1;
		case 7358:
			c.summonTime = -1;
		case 7360:
			c.summonTime = -1;
			pouchreq = 79;
			break;

		case 6812:
			c.summonTime = -1;
			pouchreq = 80;
			break;

		case 6805:
			c.summonTime = -1;
		case 7342:
			c.summonTime = -1;
			pouchreq = 83;
			break;

		case 7330:
			c.summonTime = -1;
			pouchreq = 85;
			break;
		case 6864:
			c.summonTime = -1;
			pouchreq = 86;
			break;
		case 6823:
			c.summonTime = -1;
			pouchreq = 88;
			break;
		case 7340:
			c.summonTime = -1;
			pouchreq = 89;
			break;

		case 6870:
			c.summonTime = -1;
			pouchreq = 92;
			break;

		case 7350:
			c.summonTime = -1;
			pouchreq = 93;
			break;

		case 7376:
			c.summonTime = -1;
			pouchreq = 95;
			break;
		case 6874:
			c.summonTime = -1;
			pouchreq = 96;
			break;
		case 7344:
			c.summonTime = -1;
			pouchreq = 99;
			break;
		}
		if (c.playerLevel[21] >= pouchreq) {
			Server.npcHandler.Summon(c, npcID, c.absX, c.absY - 2,
					c.heightLevel, 0, 100, maxhit, false, attack, defence);
			// c.getItems().deleteItem(c.s, 1);
			for (int i = 0; i < NPCHandler.maxNPCs; i++) {
				if (NPCHandler.npcs[i] != null) {
					c.npcslot = NPCHandler.npcs[i].npcId;
				}
				// c.getPA().sendFrame126("   " + c.playerLevel[21] + "/"+
				// c.getLevelForXP(c.playerXP[21]), 17025);
				// c.getPA().sendFrame126("" + c.summonTime / 120 +
				// ".00 Min",17021);
				if (c.hasFollower == 6870 || c.hasFollower == 6814) {
					// c.startEvent5(c);
				}
				if (c.summonTime == -1) {
					c.summonTime = 0;
				}
			}
		} else {
			c.sendMessage("You need " + pouchreq
					+ " Summoning to summon this monster.");
		}
	}
}
