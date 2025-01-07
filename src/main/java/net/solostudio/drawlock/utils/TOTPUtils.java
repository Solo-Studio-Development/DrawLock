package net.solostudio.drawlock.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@UtilityClass
@SuppressWarnings("all")
public class TOTPUtils {
    public ItemStack createMapFromQRCode(@NotNull final String data, @NotNull final Player player) {
        final var mapItem = new ItemStack(Material.FILLED_MAP);
        final var map = Bukkit.createMap(player.getWorld());

        map.getRenderers().clear();
        map.addRenderer(new MapRenderer() {
            private boolean rendered = false;

            @Override
            public void render(@NotNull final MapView map, @NotNull final MapCanvas canvas, @NotNull final Player player) {
                if (rendered) return;
                rendered = true;

                try {
                    final BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 200, 200);
                    final var byteArrayOutputStream = new ByteArrayOutputStream();
                    final var qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

                    ImageIO.write(qrImage, "PNG", byteArrayOutputStream);

                    final var qrData = byteArrayOutputStream.toByteArray();
                    final var resizedImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
                    final var graphics = resizedImage.createGraphics();

                    graphics.drawImage(qrImage, 0, 0, 128, 128, null);
                    graphics.dispose();

                    for (int x = 0; x < 128; x++) {
                        for (int y = 0; y < 128; y++) {
                            final int rgb = resizedImage.getRGB(x, y);
                            final byte mapColor = MapPalette.matchColor((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);

                            canvas.setPixel(x, y, mapColor);
                        }
                    }
                } catch (IOException | WriterException exception ) {
                    player.sendMessage("Error while rendering QR code on map! Please contact the server administrator.");
                    LoggerUtils.error(exception.getMessage());
                }
            }
        });

        final var meta = (MapMeta) mapItem.getItemMeta();

        meta.setMapView(map);
        mapItem.setItemMeta(meta);

        return mapItem;
    }

    public void generateQRCode(@NotNull String data, @NotNull String filePath) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 200, 200);

            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", new File(filePath).toPath());
        } catch (Exception exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }
}
