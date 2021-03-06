/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * ddddd
 * @version $Revision: 1.1.2.3.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class RecipeShopItemInfo  extends L2GameServerPacket
{

	private static final String _S__DA_RecipeShopItemInfo = "[S] e0 RecipeShopItemInfo";
	private int _shopId;
	private int _recipeId;


	public RecipeShopItemInfo(int shopId, int recipeId)
	{
		_shopId = shopId;
		_recipeId = recipeId;
	}

	@Override
	protected final void writeImpl()
	{
        if (!(L2World.getInstance().findObject(_shopId) instanceof L2PcInstance))
            return;

		L2PcInstance manufacturer = (L2PcInstance)L2World.getInstance().findObject(_shopId);
		writeC(0xe0);
		writeD(_shopId);
		writeD(_recipeId);
		writeD(manufacturer != null ? (int)manufacturer.getCurrentMp() : 0);
		writeD(manufacturer != null ? (int)manufacturer.getMaxMp() : 0);
		writeD(0xffffffff);
	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__DA_RecipeShopItemInfo;
	}
}
