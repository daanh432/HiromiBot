/*
 * HiromiBot, a multipurpose open source Discord bot
 * Copyright (c) 2019 - 2020 daanh432
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package nl.daanh.hiromibot.utils;

import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.util.Random;

public class RandomUtils {
    public static final Random randomGenerator = new Random();

    public static Color getRandomColor() {
        float r = randomGenerator.nextFloat();
        float g = randomGenerator.nextFloat();
        float b = randomGenerator.nextFloat();

        return new Color(r, g, b);
    }

    public static Boolean inSameVoiceChannel(Member member, Member self) {
        if (member != null && self != null && member.getVoiceState() != null && self.getVoiceState() != null) {
            return member.getVoiceState().getChannel() == self.getVoiceState().getChannel();
        }
        return false;
    }
}