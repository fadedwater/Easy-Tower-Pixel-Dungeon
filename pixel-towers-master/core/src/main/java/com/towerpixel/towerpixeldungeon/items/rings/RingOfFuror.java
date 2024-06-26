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

package com.towerpixel.towerpixeldungeon.items.rings;

import com.towerpixel.towerpixeldungeon.actors.Char;
import com.towerpixel.towerpixeldungeon.messages.Messages;
import com.towerpixel.towerpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfFuror extends Ring {

	{
		icon = ItemSpriteSheet.Icons.RING_FUROR;
	}

	public String statsInfo() {
		if (isIdentified()){
			return Messages.get(this, "stats", Messages.decimalFormat("#.##", 100f * (Math.pow(1.17f, soloBuffedBonus()) - 1f)),Messages.decimalFormat("#.##", 100f * (1-(Math.pow(0.95f, soloBuffedBonus())))));
		} else {
			return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 17f),Messages.decimalFormat("#.##", 5f));
		}
	}

	@Override
	protected RingBuff buff( ) {
		return new Furor();
	}
	
	public static float attackSpeedMultiplier(Char target ){
		return (float)Math.pow(1.17f, getBuffedBonus(target, Furor.class));
	}
	public static float attackDamageMultiplier(Char target ){
		return (float)Math.pow(0.95f, getBuffedBonus(target, Furor.class));
	}

	public class Furor extends RingBuff {
	}
}
