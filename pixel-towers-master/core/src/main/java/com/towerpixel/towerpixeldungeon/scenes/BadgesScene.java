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

package com.towerpixel.towerpixeldungeon.scenes;

import com.towerpixel.towerpixeldungeon.Assets;
import com.towerpixel.towerpixeldungeon.Badges;
import com.towerpixel.towerpixeldungeon.ShatteredPixelDungeon;
import com.towerpixel.towerpixeldungeon.messages.Messages;
import com.towerpixel.towerpixeldungeon.ui.Archs;
import com.towerpixel.towerpixeldungeon.ui.BadgesGrid;
import com.towerpixel.towerpixeldungeon.ui.ExitButton;
import com.towerpixel.towerpixeldungeon.ui.RenderedTextBlock;
import com.towerpixel.towerpixeldungeon.ui.Window;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Music;

public class BadgesScene extends PixelScene {

	@Override
	public void create() {

		super.create();

		Music.INSTANCE.playTracks(
				new String[]{Assets.Music.GNOLL_GROTTO},
				new float[]{1},
				false);

		uiCamera.visible = false;

		int w = Camera.main.width;
		int h = Camera.main.height;

		Archs archs = new Archs();
		archs.setSize( w, h );
		add( archs );

		float margin = 5;
		float top = 20;

		RenderedTextBlock title = PixelScene.renderTextBlock( Messages.get(this, "title"), 9 );
		title.hardlight(Window.TITLE_COLOR);
		title.setPos(
				(w - title.width()) / 2f,
				(top - title.height()) / 2f
		);
		align(title);
		add(title);

		Badges.loadGlobal();
		BadgesGrid grid = new BadgesGrid(true);
		grid.setRect(margin, top, w-(2*margin), h-top-margin);
		add(grid);

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );

		fadeIn();
	}

	@Override
	public void destroy() {

		Badges.saveGlobal();

		super.destroy();
	}

	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchNoFade( TitleScene.class );
	}

}
