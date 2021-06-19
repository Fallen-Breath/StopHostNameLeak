package me.fallenbreath.stop_hostname_leak.mixins;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.net.InetSocketAddress;

/**
 * The target is the lambda class extended from Thread in ConnectScreen class
 */
@Mixin(targets = "net.minecraft.client.gui.screen.ConnectScreen$1")
public abstract class ConnectScreenMixin
{
	@Unique
	private InetSocketAddress inetSocketAddress;

	@Dynamic
	@Inject(
			method = "run",
			at = @At(
					value = "INVOKE",
					target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Throwable;)V"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void storeLocals(CallbackInfo ci, InetSocketAddress inetSocketAddress, Exception exception)
	{
		this.inetSocketAddress = inetSocketAddress;
	}

	@Dynamic
	@ModifyArg(
			method = "run",
			at = @At(
					value = "INVOKE",
					target = "Ljava/lang/String;replaceAll(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
			),
			index = 0
	)
	private String useCorrectOne(String notThisOne)
	{
		return this.inetSocketAddress.toString();
	}
}
