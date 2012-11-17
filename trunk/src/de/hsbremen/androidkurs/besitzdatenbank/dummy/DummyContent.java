package de.hsbremen.androidkurs.besitzdatenbank.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyContent {

	public static class DummyItem {

		public String id;
		public String content;

		public DummyItem(String id, String content) {
			this.id = id;
			this.content = content;
		}

		@Override
		public String toString() {
			return content;
		}
	}

	public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();
	public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

	public static List<String> ELEKTRONIK = new ArrayList<String>();
	public static List<String> LEBENSMITTEL = new ArrayList<String>();
	public static List<String> FILME = new ArrayList<String>();

	static {
		ELEKTRONIK.add("Computer");
		ELEKTRONIK.add("Fernseher");
		ELEKTRONIK.add("Handy");

		LEBENSMITTEL.add("Pizza");
		LEBENSMITTEL.add("Cornflakes");
		LEBENSMITTEL.add("Cola");

		FILME.add("Stirb Langsam 1");
		FILME.add("Stirb Langsam 2");
		FILME.add("Stirb Langsam 3");
	}

	static {
		addItem(new DummyItem("1", "Item 1"));
		addItem(new DummyItem("2", "Item 2"));
		addItem(new DummyItem("3", "Item 3"));
		addItem(new DummyItem("4", System.currentTimeMillis() + ""));
	}

	private static void addItem(DummyItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	public static List<String> getList(int position) {
		switch (position) {
		case 0:
			return ELEKTRONIK;
		case 1:
			return LEBENSMITTEL;
		case 2:
			return FILME;
		default:
			return new ArrayList<String>();
		}
	}
}
