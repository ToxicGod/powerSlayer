package org.powerbot.powerslayer.wrappers;

public class Starter extends SlayerItem {
	String[] itemNames;
	
    public Starter(int amount, String... names) {
        super(SlayerItem.NOT_EQUIPPED, amount, names);
    }

    public Starter(String... names) {
        itemNames = names;
    }
    
    public String[] getNames() {
    	return itemNames;
    }
}
