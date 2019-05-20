package fr.diginamic.demo_jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import fr.diginamic.exceptions.TechnicalException;

public class TestConnexionJdbc {

	public static void main(String[] args) throws Exception {

		ResourceBundle monFichierConf = ResourceBundle.getBundle("madatabase");
		String driverName = monFichierConf.getString("madatabase.driver");
		String url = monFichierConf.getString("madatabase.url");
		String user = monFichierConf.getString("madatabase.user");
		String passwd = monFichierConf.getString("madatabase.password");

		// String url =
		// "jdbc:mysql://localhost:3306/pizzeria?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		// String user = "root";
		// String passwd = "diginamic";

		// Chargement du driver
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");// OU
														// DriverManager.registerDriver(new
														// com.mysql.cj.jdbc.Driver());
			System.out.println("Driver O.K.");
		} catch (ClassNotFoundException e) {
			throw new TechnicalException("Le driver JDBC " + driverName + "n'a pas été trouvé");
		}

		// Déclaration de la connection puis du Statement puis des ResultSet
		Connection maConnexion = null;
		Statement monStatement = null;
		ResultSet curseur = null;
		ResultSet moyenne = null;
		try {
			// Connexion à la Base de données
			maConnexion = DriverManager.getConnection(url, user, passwd);
			System.out.println(maConnexion);

			monStatement = maConnexion.createStatement();

			// Requête qui insère 4 articles dans cette nouvelle table
			monStatement.executeUpdate(
					"INSERT INTO ARTICLE (id, designation, fournisseur, prix) VALUES (1,'Clavier','Microsoft', 10)");
			monStatement.executeUpdate(
					"INSERT INTO ARTICLE (id, designation, fournisseur, prix) VALUES (2,'Stylo','BIC', 0.5)");
			monStatement.executeUpdate(
					"INSERT INTO ARTICLE (id, designation, fournisseur, prix) VALUES (3,'Champagne','Mercier', 25)");
			monStatement.executeUpdate(
					"INSERT INTO ARTICLE (id, designation, fournisseur, prix) VALUES (4,'Ballon','Decathlon', 15)");

			// Requête qui augmente les tarifs de 25% des articles de plus de
			// 10€
			monStatement.executeUpdate("UPDATE ARTICLE SET prix = prix + (prix * 0.25) WHERE prix > 10");

			// Requête qui affiche tous les articles
			curseur = monStatement.executeQuery("SELECT * FROM ARTICLE");
			while (curseur.next()) {
				System.out.println("ID: " + curseur.getInt("id") + "\nDesignation: " + curseur.getString("designation")
						+ "\nFournisseur: " + curseur.getString("fournisseur") + "\nPrix: " + curseur.getDouble("prix")
						+ "\n");
			}

			// Requête qui extrait la moyenne des prix des articles et affiche
			// cette moyenne
			moyenne = monStatement.executeQuery("SELECT AVG(prix) FROM ARTICLE");
			while (moyenne.next()) {
				System.out.println("Moyenne des prix des articles: " + moyenne.getDouble(1));
			}

			// Requête qui supprime tous les articles de la base de données afin
			// que la classe soit rejouable.
			monStatement.executeUpdate("TRUNCATE TABLE ARTICLE;");

		} catch (SQLException e) {
			throw new TechnicalException("La connexion à la base n'a pas pu etre établi");
		} finally {
			try {
				// fermeture des ResultSet
				if (curseur != null) {
					curseur.close();
				}

				if (moyenne != null) {
					moyenne.close();
				}

				// Fermeture des Statements
				if (monStatement != null) {
					monStatement.close();
				}

				// fermeture de la connexion JDBC
				if (maConnexion != null) {
					maConnexion.close();
				}
			} catch (SQLException e) {
				throw new TechnicalException("La déconnexion à la base a échoué");
			}
		}

	}

}
