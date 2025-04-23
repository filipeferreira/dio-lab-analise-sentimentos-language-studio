package sentimentopinion;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.ai.textanalytics.models.AnalyzeSentimentOptions;
import com.azure.ai.textanalytics.models.AssessmentSentiment;
import com.azure.ai.textanalytics.models.DocumentSentiment;
import com.azure.ai.textanalytics.models.SentimentConfidenceScores;
import com.azure.ai.textanalytics.models.TargetSentiment;
import com.azure.core.credential.AzureKeyCredential;

public class Main {

    public static void main(String[] args) {
        String languageKey = "1GZwBKhU9neNNv3dkgdLbmjurEkqdNlQR6T4KbdIVTh0FU7s7oFQJQQJ99BDACZoyfiXJ3w3AAAaACOGv3OL";
        String languageEndpoint = "https://dio-language-lab.cognitiveservices.azure.com/";
        TextAnalyticsClient client = authenticateClient(languageKey, languageEndpoint);
        sentimentAnalysisWithOpinionMiningExample(client);
    }
    // Method to authenticate the client object with your key and endpoint.
    static TextAnalyticsClient authenticateClient(String key, String endpoint) {
        return new TextAnalyticsClientBuilder()
                .credential(new AzureKeyCredential(key))
                .endpoint(endpoint)
                .buildClient();
    }
    // Example method for detecting sentiment and opinions in text.
    static void sentimentAnalysisWithOpinionMiningExample(TextAnalyticsClient client)
    {
        // The document that needs be analyzed.
        String document = "The food and service were unacceptable. The concierge was nice, however.";

        System.out.printf("Document = %s%n", document);

        AnalyzeSentimentOptions options = new AnalyzeSentimentOptions().setIncludeOpinionMining(true);
        final DocumentSentiment documentSentiment = client.analyzeSentiment(document, "en", options);
        SentimentConfidenceScores scores = documentSentiment.getConfidenceScores();
        System.out.printf(
                "Recognized document sentiment: %s, positive score: %f, neutral score: %f, negative score: %f.%n",
                documentSentiment.getSentiment(), scores.getPositive(), scores.getNeutral(), scores.getNegative());


        documentSentiment.getSentences().forEach(sentenceSentiment -> {
            SentimentConfidenceScores sentenceScores = sentenceSentiment.getConfidenceScores();
            System.out.printf("\tSentence sentiment: %s, positive score: %f, neutral score: %f, negative score: %f.%n",
                    sentenceSentiment.getSentiment(), sentenceScores.getPositive(), sentenceScores.getNeutral(), sentenceScores.getNegative());
            sentenceSentiment.getOpinions().forEach(opinion -> {
                TargetSentiment targetSentiment = opinion.getTarget();
                System.out.printf("\t\tTarget sentiment: %s, target text: %s%n", targetSentiment.getSentiment(),
                        targetSentiment.getText());
                for (AssessmentSentiment assessmentSentiment : opinion.getAssessments()) {
                    System.out.printf("\t\t\t'%s' assessment sentiment because of \"%s\". Is the assessment negated: %s.%n",
                            assessmentSentiment.getSentiment(), assessmentSentiment.getText(), assessmentSentiment.isNegated());
                }
            });
        });
    }
}
