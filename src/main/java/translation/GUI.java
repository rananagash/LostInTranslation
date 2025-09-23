package translation;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

// Task D: Updated GUI with JComboBox for languages and JList for countries
//         with real-time translation updates
public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create converter instances
            LanguageCodeConverter languageConverter = new LanguageCodeConverter();
            CountryCodeConverter countryConverter = new CountryCodeConverter();
            JSONTranslator translator = new JSONTranslator();

            // Language selection panel
            JPanel languagePanel = new JPanel();
            languagePanel.add(new JLabel("Language:"));

            JComboBox<String> languageComboBox = new JComboBox<>();
            // Add all supported language names to the combo box
            for (String languageCode : translator.getLanguageCodes()) {
                String languageName = languageConverter.fromLanguageCode(languageCode);
                if (languageName != null) {
                    languageComboBox.addItem(languageName);
                }
            }
            languagePanel.add(languageComboBox);

            // Country selection panel
            JPanel countryPanel = new JPanel();
            countryPanel.setLayout(new BorderLayout());
            countryPanel.add(new JLabel("Country:"), BorderLayout.NORTH);

            // Create country names array
            List<String> countryCodes = translator.getCountryCodes();
            String[] countryNames = new String[countryCodes.size()];
            for (int i = 0; i < countryCodes.size(); i++) {
                String countryName = countryConverter.fromCountryCode(countryCodes.get(i));
                countryNames[i] = countryName != null ? countryName : countryCodes.get(i);
            }

            // Create JList and put it in a scroll pane
            JList<String> countryList = new JList<>(countryNames);
            countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane countryScrollPane = new JScrollPane(countryList);
            countryPanel.add(countryScrollPane, BorderLayout.CENTER);

            // Result display panel
            JPanel resultPanel = new JPanel();
            resultPanel.add(new JLabel("Translation:"));
            JLabel resultLabel = new JLabel("Select a country and language");
            resultLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            resultPanel.add(resultLabel);

            // Language selection listener
            languageComboBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateTranslation(languageComboBox, countryList, resultLabel,
                            languageConverter, countryConverter, translator);
                }
            });

            // Country selection listener
            countryList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    updateTranslation(languageComboBox, countryList, resultLabel,
                            languageConverter, countryConverter, translator);
                }
            });

            // Main panel
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(languagePanel);
            mainPanel.add(countryPanel);
            mainPanel.add(resultPanel);

            // Create window
            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /**
     * Updates the translation result based on selected language and country
     */
    private static void updateTranslation(JComboBox<String> languageComboBox,
                                          JList<String> countryList,
                                          JLabel resultLabel,
                                          LanguageCodeConverter languageConverter,
                                          CountryCodeConverter countryConverter,
                                          JSONTranslator translator) {

        String selectedLanguage = (String) languageComboBox.getSelectedItem();
        String selectedCountry = countryList.getSelectedValue();

        if (selectedLanguage != null && selectedCountry != null) {
            // Convert language name to code
            String languageCode = languageConverter.fromLanguage(selectedLanguage);
            // Convert country name to code
            String countryCode = countryConverter.fromCountry(selectedCountry);

            if (languageCode != null && countryCode != null) {
                String translation = translator.translate(countryCode, languageCode);
                resultLabel.setText(translation != null ? translation : "Translation not found");
            } else {
                resultLabel.setText("Invalid selection");
            }
        } else {
            resultLabel.setText("Select a country and language");
        }
    }
}