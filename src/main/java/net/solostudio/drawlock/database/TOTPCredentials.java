package net.solostudio.drawlock.database;

import com.warrenstrange.googleauth.ICredentialRepository;
import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.interfaces.DrawLockDatabase;
import net.solostudio.drawlock.utils.LoggerUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.google.gson.Gson;

public class TOTPCredentials implements ICredentialRepository {
    private final DrawLockDatabase database = DrawLock.getDatabase();
    private final Gson gson = new Gson();

    @Override
    public String getSecretKey(@NotNull String username) {
        String query = "SELECT SECRET FROM totp WHERE USERNAME = ?";
        try (PreparedStatement preparedStatement = database.getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return resultSet.getString("SECRET");
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
        return null;
    }

    @Override
    public void saveUserCredentials(@NotNull String username, @NotNull String secretKey, int validationCode, List<Integer> scratchCodes) {
        String query = "INSERT INTO totp (USERNAME, SECRET, SCRATCH_CODES) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE SECRET = ?, SCRATCH_CODES = ?";

        try (PreparedStatement preparedStatement = database.getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, secretKey);
            preparedStatement.setString(3, gson.toJson(scratchCodes));
            preparedStatement.setString(4, secretKey);
            preparedStatement.setString(5, gson.toJson(scratchCodes));
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

}
