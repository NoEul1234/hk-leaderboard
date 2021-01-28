package com.noeul.discord.hk.leaderboard.command;

import com.noeul.discord.hk.leaderboard.Leaderboard;
import com.noeul.discord.hk.leaderboard.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;

import java.awt.*;
import java.util.Arrays;

public class LevelTableCommand extends Command {
	public LevelTableCommand(Guild guild, MessageChannel room, User sender, String[] args) {
		super(guild, room, sender, args);
	}

	public void run() {
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(Color.HSBtoRGB((float) Math.random(), 1.0F, 1.0F))
				.setFooter(Main.getProfile().getAsTag() + " • Powered by 노을", Main.getProfile().getAvatarUrl());

		try {
			int page = args.length != 0 ? Integer.parseInt(args[0]) : 1;
			if (page < 1 || page > Leaderboard.MAX_PAGE) throw new NumberFormatException();

			room.sendMessage(
					embedBuilder.setAuthor("HK Level Table", null, guild.getIconUrl())
							.setDescription("```ml\n" + Leaderboard.getLevelTable(page) + "\n```")
							.setFooter("페이지 " + page + "/" + Leaderboard.MAX_PAGE, Main.getProfile().getAvatarUrl())
							.build()
			).queue(($) -> Arrays.asList("⏮", "⏪", "◀", "▶", "⏩", "⏭").forEach(s -> $.addReaction(s).queue()));
		} catch (NumberFormatException e) {
			room.sendMessage(
					embedBuilder.setAuthor(args[0] + "은(는) 올바른 매개변수가 아닙니다", null, guild.getIconUrl())
							.setDescription("1에서 " + Leaderboard.MAX_PAGE + " 사이의 자연수를 입력해 주세요.")
							.build()
			).queue();
		}

	}

	public static class ReactionClickEventListener {
		public ReactionClickEventListener(GuildMessageReactionAddEvent event) {
			Message msg = event.retrieveMessage().complete();
			if (
					msg.getAuthor().getId().equals(Main.getProfile().getId())
					&& !event.getUserId().equals(Main.getProfile().getId())
			) changePage(msg, event.getUserId(), msg.getEmbeds().get(0), event.getReactionEmote());
		}

		public ReactionClickEventListener(GuildMessageReactionRemoveEvent event) {
			Message msg = event.retrieveMessage().complete();
			if (
					msg.getAuthor().getId().equals(Main.getProfile().getId())
					&& !event.getUserId().equals(Main.getProfile().getId())
			) changePage(msg, event.getUserId(), msg.getEmbeds().get(0), event.getReactionEmote());
		}

		private void changePage(Message msg, String userId, MessageEmbed embed, ReactionEmote emote) {
			int page = Integer.parseInt(embed.getFooter().getText().split("\\s+", 2)[1].split("/", 2)[0]);

			switch(emote.getEmoji()) {
			case "⏮": page = 1; break;
			case "⏪": page = Math.max(Math.min(page - 5, Leaderboard.MAX_PAGE), 1); break;
			case "◀": page = Math.max(Math.min(page - 1, Leaderboard.MAX_PAGE), 1); break;
			case "▶": page = Math.max(Math.min(page + 1, Leaderboard.MAX_PAGE), 1); break;
			case "⏩": page = Math.max(Math.min(page + 5, Leaderboard.MAX_PAGE), 1); break;
			case "⏭": page = Leaderboard.MAX_PAGE; break;
			}

			msg.editMessage(
					new EmbedBuilder(embed)
							.setDescription("```ml\n" + Leaderboard.getLevelTable(page) + "\n```")
							.setFooter("페이지 " + page + "/" + 33)
							.build()
			).queue();
		}
	}
}
