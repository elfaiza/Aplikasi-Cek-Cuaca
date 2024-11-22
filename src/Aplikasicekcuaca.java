/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Disporapar HST
 */
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Aplikasicekcuaca extends JFrame {
    private JComboBox<String> comboBoxKota;
    private JTextField txtKotaBaru;
    private JButton btnCekCuaca, btnSimpan, btnTambahKota;
    private JLabel lblCuaca, lblIkon;
    private JPanel panelUtama;

    private final String API_KEY = "753777486ab9abe99854797e9d21ff7b";
    private final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    public Aplikasicekcuaca() {
        setTitle("Aplikasi Cek Cuaca");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        panelUtama = new JPanel();
        panelUtama.setLayout(new GridLayout(6, 1));

        // Input kota
        comboBoxKota = new JComboBox<>(new String[]{"Jakarta", "Surabaya", "Bandung"});
        txtKotaBaru = new JTextField();
        btnTambahKota = new JButton("Tambah Kota");

        // Tombol dan label
        btnCekCuaca = new JButton("Cek Cuaca");
        btnSimpan = new JButton("Simpan Data");
        lblCuaca = new JLabel("Cuaca: -");
        lblIkon = new JLabel();

        // Tambah listener untuk tombol
        btnTambahKota.addActionListener(e -> {
            String kotaBaru = txtKotaBaru.getText().trim();
            if (!kotaBaru.isEmpty() && !kotaSudahAda(kotaBaru)) {
                comboBoxKota.addItem(kotaBaru);
                txtKotaBaru.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Kota sudah ada atau kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCekCuaca.addActionListener(e -> cekCuaca((String) comboBoxKota.getSelectedItem()));

        btnSimpan.addActionListener(e -> simpanData(lblCuaca.getText()));

        // Menambahkan komponen ke panel
        panelUtama.add(new JLabel("Pilih Kota:"));
        panelUtama.add(comboBoxKota);
        panelUtama.add(txtKotaBaru);
        panelUtama.add(btnTambahKota);
        panelUtama.add(btnCekCuaca);
        panelUtama.add(lblCuaca);
        panelUtama.add(lblIkon);
        panelUtama.add(btnSimpan);

        // Menambahkan panel ke frame
        add(panelUtama);
    }

    private boolean kotaSudahAda(String kota) {
        for (int i = 0; i < comboBoxKota.getItemCount(); i++) {
            if (comboBoxKota.getItemAt(i).equalsIgnoreCase(kota)) {
                return true;
            }
        }
        return false;
    }

    private void cekCuaca(String kota) {
        try {
            // Membuat URL API
            String urlString = BASE_URL + "?q=" + kota + "&appid=" + API_KEY + "&units=metric";
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Membaca respons JSON
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(url.openStream());
                StringBuilder json = new StringBuilder();
                while (scanner.hasNext()) {
                    json.append(scanner.nextLine());
                }
                scanner.close();

                // Parsing JSON menggunakan GSON
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(json.toString(), JsonObject.class);

                // Mengambil data cuaca
                String kondisiCuaca = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
                String ikon = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("icon").getAsString();
                String suhu = jsonObject.getAsJsonObject("main").get("temp").getAsString();

                // Menampilkan hasil
                lblCuaca.setText("Cuaca di " + kota + ": " + kondisiCuaca + ", " + suhu + "°C");
                lblIkon.setIcon(new ImageIcon(new URL("http://openweathermap.org/img/w/" + ikon + ".png")));
            } else {
                lblCuaca.setText("Gagal mengambil data cuaca!");
            }
        } catch (Exception e) {
            lblCuaca.setText("Error: " + e.getMessage());
        }
    }

    private void simpanData(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data_cuaca.txt", true))) {
            writer.write(data);
            writer.newLine();
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Aplikasicekcuaca().setVisible(true));
    }
}
