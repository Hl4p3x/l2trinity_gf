/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.model.zone.type;

import ghosts.model.Ghost;
import luna.custom.globalScheduler.GlobalEventVariablesHolder;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.GameTimeController;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;

/**
 * @author Gnat
 */
public class L2PaganZone extends L2ZoneType
{
	public L2PaganZone(final int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(final L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			if (Config.ENABLE_OLD_NIGHT)
			{
				if (!GameTimeController.getInstance().isNowNight() && !character.isGM())
				{
					character.setInsideZone(L2Character.ZONE_CHAOTIC, false);
					character.getActingPlayer().setIsInFT(false);
					character.sendMessage("Pagan's Temple is a night zone!");
					character.setIsPendingRevive(true);
					character.teleToLocation(83380, 148107, -3404, true);
					return;
				}
			}
			else
			{
				if (!GlobalEventVariablesHolder.getInstance().getNight())
				{
					character.setInsideZone(L2Character.ZONE_CHAOTIC, false);
					character.getActingPlayer().setIsInFT(false);
					character.sendMessage("Pagan's Temple is a night zone!");
					character.setIsPendingRevive(true);
					character.teleToLocation(83380, 148107, -3404, true);
					return;
				}
			}
			if (Config.HWID_FARMZONES_CHECK && !character.isGM())
			{
				String hwid = ((L2PcInstance) character).getHWID();
				for (L2PcInstance player : L2World.getInstance().getAllPlayers().values())
				{

					if (!(player instanceof Ghost) && player.getClient().isDetached())
					{
						continue;
					}
					if (!(player.isInsideZone(L2Character.ZONE_FARM) || player.isInsideZone(L2Character.ZONE_RAID) || player.isInHuntersVillage() || player.isInHuntersVillage()))
					{
						continue;
					}
					if (player == character)
					{
						continue;
					}
					if (player.isGM() || character.isGM())
					{
						continue;
					}
					String plr_hwid = player.getClient().getStrixClientData().getClientHWID();
					if (plr_hwid.equalsIgnoreCase(hwid))
					{
						character.setIsPendingRevive(true);
						character.teleToLocation(83380, 148107, -3404, true);
						character.setInsideZone(L2Character.ZONE_FARM, false);
						character.sendMessage("You have another window in a hwid restricted zone.");
						break;
					}
					else
					{
						if (character.isGM())
						{
							character.sendMessage("You have entered the Farm Area");
						}
					}
				}
			}
			if (_id == 60003 && !character.getActingPlayer().isInFT()) // FT
			{
				character.setInsideZone(L2Character.ZONE_FARM, true);
				character.setInsideZone(L2Character.ZONE_CHAOTIC, true);
				character.getActingPlayer().setIsInFT(true);
				character.sendMessage("You have entered a chaotic zone, where no penalty is applied upon death with karma and karma gain is reduced by 40%");
				character.sendMessage("You have entered Pagan's Temple grounds, you cannot use S grade or below weapons or armors");
				if (!character.isGM())
				{
					boolean update = false;
					for (L2ItemInstance item : character.getInventory().getItems())
					{
						if (item != null && item.isEquipped())
						{
							if (item.isARestrictedItemFT())
							{
								if (item.isAugmented())
									item.getAugmentation().removeBonus(character.getActingPlayer());
								L2ItemInstance[] unequiped = character.getInventory().unEquipItemInBodySlotAndRecord(character.getInventory().getSlotFromItem(item));
								InventoryUpdate iu = new InventoryUpdate();
								for (L2ItemInstance element : unequiped)
									iu.addModifiedItem(element);
								character.sendPacket(iu);
								update = true;
							}
						}
					}
					if (update)
						character.getActingPlayer().broadcastUserInfo();
				}
			}
		}
	}
	
	@Override
	protected void onExit(final L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			character.setInsideZone(L2Character.ZONE_FARM, false);
			if (character.isGM())
			{
				character.sendMessage("You have left the Farm Area");
			}
			final L2PcInstance player = (L2PcInstance) character;
			player.setInPvPCustomEventZone(false);
			player.setInsideZone(L2Character.ZONE_CHAOTIC, false);
			player.sendMessage("You have left a chaotic zone");
			player.setIsInFT(false);
			if (_id == 60003) // FT
			{
				player.setIsInFT(false);
			}
		}
	}
	
	@Override
	public void onDieInside(final L2Character character)
	{}
	
	@Override
	public void onReviveInside(final L2Character character)
	{}
}
