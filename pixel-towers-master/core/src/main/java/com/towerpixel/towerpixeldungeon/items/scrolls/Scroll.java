/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.towerpixel.towerpixeldungeon.items.scrolls;

import com.towerpixel.towerpixeldungeon.actors.buffs.Blindness;
import com.towerpixel.towerpixeldungeon.actors.buffs.Buff;
import com.towerpixel.towerpixeldungeon.actors.buffs.Invisibility;
import com.towerpixel.towerpixeldungeon.actors.buffs.MagicImmune;
import com.towerpixel.towerpixeldungeon.actors.buffs.ScrollEmpower;
import com.towerpixel.towerpixeldungeon.actors.hero.Hero;
import com.towerpixel.towerpixeldungeon.actors.hero.Talent;
import com.towerpixel.towerpixeldungeon.items.Generator;
import com.towerpixel.towerpixeldungeon.items.Item;
import com.towerpixel.towerpixeldungeon.items.ItemStatusHandler;
import com.towerpixel.towerpixeldungeon.items.Recipe;
import com.towerpixel.towerpixeldungeon.items.artifacts.UnstableSpellbook;
import com.towerpixel.towerpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.towerpixel.towerpixeldungeon.items.scrolls.exotic.ScrollOfAntiMagic;
import com.towerpixel.towerpixeldungeon.items.stones.Runestone;
import com.towerpixel.towerpixeldungeon.items.stones.StoneOfFear;
import com.towerpixel.towerpixeldungeon.items.stones.StoneOfAggression;
import com.towerpixel.towerpixeldungeon.items.stones.StoneOfAugmentation;
import com.towerpixel.towerpixeldungeon.items.stones.StoneOfBlast;
import com.towerpixel.towerpixeldungeon.items.stones.StoneOfBlink;
import com.towerpixel.towerpixeldungeon.items.stones.StoneOfClairvoyance;
import com.towerpixel.towerpixeldungeon.items.stones.StoneOfDeepSleep;
import com.towerpixel.towerpixeldungeon.items.stones.StoneOfDisarming;
import com.towerpixel.towerpixeldungeon.items.stones.StoneOfEnchantment;
import com.towerpixel.towerpixeldungeon.items.stones.StoneOfFlock;
import com.towerpixel.towerpixeldungeon.items.stones.StoneOfIntuition;
import com.towerpixel.towerpixeldungeon.items.stones.StoneOfShock;
import com.towerpixel.towerpixeldungeon.messages.Messages;
import com.towerpixel.towerpixeldungeon.sprites.HeroSprite;
import com.towerpixel.towerpixeldungeon.sprites.ItemSpriteSheet;
import com.towerpixel.towerpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public abstract class Scroll extends Item {

	{
		isIdentified();
	}
	
	public static final String AC_READ	= "READ";
	
	protected static final float TIME_TO_READ	= 1f;

	private static final LinkedHashMap<String, Integer> runes = new LinkedHashMap<String, Integer>() {
		{
			put("KAUNAN",ItemSpriteSheet.SCROLL_KAUNAN);
			put("SOWILO",ItemSpriteSheet.SCROLL_SOWILO);
			put("LAGUZ",ItemSpriteSheet.SCROLL_LAGUZ);
			put("YNGVI",ItemSpriteSheet.SCROLL_YNGVI);
			put("GYFU",ItemSpriteSheet.SCROLL_GYFU);
			put("RAIDO",ItemSpriteSheet.SCROLL_RAIDO);
			put("ISAZ",ItemSpriteSheet.SCROLL_ISAZ);
			put("MANNAZ",ItemSpriteSheet.SCROLL_MANNAZ);
			put("NAUDIZ",ItemSpriteSheet.SCROLL_NAUDIZ);
			put("BERKANAN",ItemSpriteSheet.SCROLL_BERKANAN);
			put("ODAL",ItemSpriteSheet.SCROLL_ODAL);
			put("TIWAZ",ItemSpriteSheet.SCROLL_TIWAZ);
		}
	};
	
	protected static ItemStatusHandler<Scroll> handler;
	
	protected String rune;
	
	{
		stackable = true;
		defaultAction = AC_READ;
	}
	
	@SuppressWarnings("unchecked")
	public static void initLabels() {
		handler = new ItemStatusHandler<>( (Class<? extends Scroll>[])Generator.Category.SCROLL.classes, runes );
	}
	
	public static void save( Bundle bundle ) {
		handler.save( bundle );
	}

	public static void saveSelectively( Bundle bundle, ArrayList<Item> items ) {
		ArrayList<Class<?extends Item>> classes = new ArrayList<>();
		for (Item i : items){
			if (i instanceof ExoticScroll){
				if (!classes.contains(ExoticScroll.exoToReg.get(i.getClass()))){
					classes.add(ExoticScroll.exoToReg.get(i.getClass()));
				}
			} else if (i instanceof Scroll){
				if (!classes.contains(i.getClass())){
					classes.add(i.getClass());
				}
			}
		}
		handler.saveClassesSelectively( bundle, classes );
	}

	@SuppressWarnings("unchecked")
	public static void restore( Bundle bundle ) {
		handler = new ItemStatusHandler<>( (Class<? extends Scroll>[])Generator.Category.SCROLL.classes, runes, bundle );
	}
	
	public Scroll() {
		super();
		reset();
	}
	
	//anonymous scrolls are always IDed, do not affect ID status,
	//and their sprite is replaced by a placeholder if they are not known,
	//useful for items that appear in UIs, or which are only spawned for their effects
	protected boolean anonymous = false;
	
	
	@Override
	public void reset(){
		super.reset();
		if (handler != null && handler.contains(this)) {
			image = handler.image(this);
			rune = handler.label(this);
		}
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_READ );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_READ )) {
			
			if (hero.buff(MagicImmune.class) != null){
				GLog.w( Messages.get(this, "no_magic") );
			} else if (hero.buff( Blindness.class ) != null) {
				GLog.w( Messages.get(this, "blinded") );
			} else if (hero.buff(UnstableSpellbook.bookRecharge.class) != null
					&& hero.buff(UnstableSpellbook.bookRecharge.class).isCursed()
					&& !(this instanceof ScrollOfRemoveCurse || this instanceof ScrollOfAntiMagic)){
				GLog.n( Messages.get(this, "cursed") );
			} else {
				curUser = hero;
				curItem = detach( hero.belongings.backpack );
				doRead();
			}
			
		}
	}
	
	public abstract void doRead();

	protected void readAnimation() {
		Invisibility.dispel();
		curUser.spend( TIME_TO_READ );
		curUser.busy();
		((HeroSprite)curUser.sprite).read();

		if (curUser.hasTalent(Talent.EMPOWERING_SCROLLS)){
			Buff.affect(curUser, ScrollEmpower.class).reset();
			updateQuickslot();
		}
	}

	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	public static HashSet<Class<? extends Scroll>> getKnown() {
		return handler.known();
	}
	
	public static HashSet<Class<? extends Scroll>> getUnknown() {
		return handler.unknown();
	}
	
	public static boolean allKnown() {
		return handler.known().size() == Generator.Category.SCROLL.classes.length;
	}
	
	@Override
	public int value() {
		return 30 * quantity;
	}

	@Override
	public int energyVal() {
		return 6 * quantity;
	}
	
	public static class PlaceHolder extends Scroll {
		
		{
			image = ItemSpriteSheet.SCROLL_HOLDER;
		}
		
		@Override
		public boolean isSimilar(Item item) {
			return ExoticScroll.regToExo.containsKey(item.getClass())
					|| ExoticScroll.regToExo.containsValue(item.getClass());
		}
		
		@Override
		public void doRead() {}
		
		@Override
		public String info() {
			return "";
		}
	}
	
	public static class ScrollToStone extends Recipe {
		
		private static HashMap<Class<?extends Scroll>, Class<?extends Runestone>> stones = new HashMap<>();
		static {
			stones.put(ScrollOfIdentify.class,      StoneOfIntuition.class);
			stones.put(ScrollOfLullaby.class,       StoneOfDeepSleep.class);
			stones.put(ScrollOfMagicMapping.class,  StoneOfClairvoyance.class);
			stones.put(ScrollOfMirrorImage.class,   StoneOfFlock.class);
			stones.put(ScrollOfRetribution.class,   StoneOfBlast.class);
			stones.put(ScrollOfRage.class,          StoneOfAggression.class);
			stones.put(ScrollOfRecharging.class,    StoneOfShock.class);
			stones.put(ScrollOfRemoveCurse.class,   StoneOfDisarming.class);
			stones.put(ScrollOfTeleportation.class, StoneOfBlink.class);
			stones.put(ScrollOfTerror.class,        StoneOfFear.class);
			stones.put(ScrollOfTransmutation.class, StoneOfAugmentation.class);
			stones.put(ScrollOfUpgrade.class,       StoneOfEnchantment.class);
		}
		
		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			if (ingredients.size() != 1
					|| !(ingredients.get(0) instanceof Scroll)
					|| !stones.containsKey(ingredients.get(0).getClass())){
				return false;
			}
			
			return true;
		}
		
		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 0;
		}
		
		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			
			Scroll s = (Scroll) ingredients.get(0);
			
			s.quantity(s.quantity() - 1);
			s.identify();
			
			return Reflection.newInstance(stones.get(s.getClass())).quantity(2);
		}
		
		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			
			Scroll s = (Scroll) ingredients.get(0);
			return Reflection.newInstance(stones.get(s.getClass())).quantity(2);

		}
	}
}