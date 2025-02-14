/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package bleach.hack.event.events;

import bleach.hack.event.Event;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleEffect;

public class EventParticle extends Event {

	public static class Normal extends EventParticle {

		public Particle particle;

		public Normal(Particle particle) {
			this.particle = particle;
		}
	}

	public static class Emitter extends EventParticle {

		public ParticleEffect effect;

		public Emitter(ParticleEffect effect) {
			this.effect = effect;
		}
	}
}
