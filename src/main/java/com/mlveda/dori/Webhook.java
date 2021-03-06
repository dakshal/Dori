/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlveda.dori;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mlveda.disneychatbot.config.ApiService;
import com.mlveda.disneychatbot.config.AppConstant;
import com.mlveda.disneychatbot.config.MLVedaResponse;
import com.mlveda.disneychatbot.config.RestClient;
import com.mlveda.disneychatbot.database.MongoProvider;
import com.mlveda.disneychatbot.models.Attachments;
import com.mlveda.disneychatbot.models.Button;
import com.mlveda.disneychatbot.models.Collection;
import com.mlveda.disneychatbot.models.CollectionModel;
import com.mlveda.disneychatbot.models.Element;
import com.mlveda.disneychatbot.models.Entry;
import com.mlveda.disneychatbot.models.Message;
import com.mlveda.disneychatbot.models.Messaging;
import com.mlveda.disneychatbot.models.FBChatBotWebhookResponse;
import com.mlveda.disneychatbot.models.Image;
import com.mlveda.disneychatbot.models.Option;
import com.mlveda.disneychatbot.models.PagesInsertData;
import com.mlveda.disneychatbot.models.Payload;
import com.mlveda.disneychatbot.models.PayloadButton;
import com.mlveda.disneychatbot.models.Product;
import com.mlveda.disneychatbot.models.ProductCollectionMapping;
import com.mlveda.disneychatbot.models.Recipient;
import com.mlveda.disneychatbot.models.RedirectButton;
import com.mlveda.disneychatbot.models.ResponseModel;
import com.mlveda.disneychatbot.models.SendAttachment;
import com.mlveda.disneychatbot.models.SendAttachmentMessage;
import com.mlveda.disneychatbot.models.SendAttachments;
import com.mlveda.disneychatbot.models.SendMessage;
import com.mlveda.disneychatbot.models.SendPayload;
import com.mlveda.disneychatbot.models.TextMessageResponse;
import com.mlveda.disneychatbot.models.Variant;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.Document;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 *
 * @author incredible
 */
@WebServlet(name = "Webhook", urlPatterns = {"/webhook"})
public class Webhook extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private final Gson gson = new Gson();
//    private final ApiService service = RestClient.instance.getApiService();

//    public String[] elementTitle = new String[]{"Casio Scientific", "Cltllzen CT-512 Basic", "Casio FX991ES Plus Scientific Calculator", "Cltllzen Notebook Type Scientific", "Texas Instruments BA II Plus Financial"};
//    public String[] elementSubTitle = new String[]{"Price Rs. 390 + Rs. 50 delivery charge\n4.4 user rating", "Price Rs. 100 + Rs. 100 delivery charge\n3.5 user rating", "Price Rs. 895 + Rs. 100 delivery charge\n4.7 user rating", "Price Rs. 300 + Rs. 50 delivery charge\n3.7 user rating", "Price Rs. 2,850 + Rs. 100 delivery charge\n4.4 user rating"};
//    public String[] elementItemURL = new String[]{"http://www.flipkart.com/casio-scientific/p/itmd42zcderygges?pid=CALEAXGWBZRNMED9&al=0VV%2Fo0h0R%2FlQGaNytcUSkMldugMWZuE75aUsiwTbcEN%2FtbeNXjslkocG%2BQexgPVO%2BkeMe8Sn%2FR8%3D&ref=L%3A-199285331275200905&srno=p_1&findingMethod=Search&otracker=start", "http://www.flipkart.com/cltllzen-ct-512-basic/p/itme7zc58fpphwtv?pid=CALE7ZC52F2MW2DZ&al=0VV%2Fo0h0R%2FlQGaNytcUSkMldugMWZuE75aUsiwTbcEM%2FDyl0zmgBbv0vHpiFgsF1%2FB5AahOzqsI%3D&ref=L%3A-199285331275200905&srno=p_3&otracker=from-search", "http://www.flipkart.com/casio-fx991es-plus-scientific-calculator/p/itmdcmmvuzgsdrsk?pid=CALDCMMV9NTQHR2H&al=0VV%2Fo0h0R%2FlQGaNytcUSkMldugMWZuE75aUsiwTbcENbOwAyMJf4U%2Fd4%2FtWZ1ElZmuEmDgnCohc%3D&ref=L%3A-199285331275200905&srno=p_5&otracker=from-search", "http://www.flipkart.com/cltllzen-notebook-type-scientific/p/itme7ayvtpqnzpzg?pid=CALE7AYVTBFBJ94C&al=0VV%2Fo0h0R%2FlQGaNytcUSkMldugMWZuE75aUsiwTbcENjQomTUF8KpiBi0HZoixj9Jtp6x6APD1Y%3D&ref=L%3A-199285331275200905&srno=p_6&otracker=from-search", "http://www.flipkart.com/texas-instruments-ba-ii-plus-financial/p/itmd42zjwwj2de2b?pid=CALD42ZGAYH3AZH4&al=0VV%2Fo0h0R%2FntY%2FWp4sZ0i8ldugMWZuE75aUsiwTbcEMeyG07ZeVS3JVZ97IrGvQOyCCvGp7pBKE%3D&ref=L%3A-199285331275200905&srno=p_11&otracker=from-search"};
//    public String[] elementImageURL = new String[]{"http://img5a.flixcart.com/image/calculator/e/d/9/casio-fx-82ms-400x400-imaeax7vasgcd6eg.jpeg", "http://img5a.flixcart.com/image/calculator/j/a/v/cltllzen-ct-512-ct-512-black-400x400-imae7ygyqsh55ph6.jpeg", "http://img5a.flixcart.com/image/calculator/r/2/h/casio-fx991es-plus-fx991es-plus-400x400-imadnzghbjmaqyuw.jpeg", "http://img5a.flixcart.com/image/calculator/9/4/c/cltllzen-notebook-type-ct-8814v-w-400x400-imae79ydb6qtwvvd.jpeg", "http://img6a.flixcart.com/image/calculator/z/h/4/texas-instruments-ba-ii-ba-ii-plus-400x400-imadnzj3uvfg7xvc.jpeg"};
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("Inside processRequest");
        System.out.println("request is as follows : ");
        System.out.println("---------------------------------");
        System.out.println("---------------------------------");
        String requestParams = getBody(request);
        System.out.println(requestParams);
        System.out.println("---------------------------------");
        System.out.println("---------------------------------");

        if (request.getMethod().equals("GET")) {
            webhookSetupDetails(request, response);
        } else if (request.getMethod().equals("POST")) {

            //String requestParams = getBody(request);
            //System.out.println(requestParams);
            FBChatBotWebhookResponse body = gson.fromJson(requestParams, FBChatBotWebhookResponse.class);

            if (body.getObject().equals("page")) {
                ArrayList<Entry> entry = body.getEntry();

                if (entry.size() > 0) {
                    for (int i = 0; i < entry.size(); i++) {
                        ArrayList<Messaging> message = entry.get(i).getMessaging();
                        for (int j = 0; j < message.size(); j++) {
                            Messaging event = message.get(j);
                            if (event.getOptin() != null) {

                            } else if (event.getMessage() != null) {

                                receivedMessage(event);

                            } else if (event.getDelivery() != null) {

                            } else if (event.getPostback() != null) {
                                receivedPostback(event);
                            } else {
                                System.err.println("else is received" + gson.toJson(event));
                            }
                        }
                    }
                } else {
                    System.err.println("Entry size is zero");
                }
            }

//            String payloadRequest = getBody(request);
//            System.out.println(payloadRequest);
//            JSONObject body = new JSONObject(payloadRequest);
//            if (body.get("object").equals("page")) {
//                JSONArray entryArray = body.getJSONArray("entry");
//                for (int i = 0; i < entryArray.length(); i++) {
//                    JSONObject object = entryArray.getJSONObject(i);
//
//                    String pageID = object.getString("id");
//                    long pageTime = object.getLong("time");
//
//                    JSONArray messageEvent = object.getJSONArray("messaging");
//
//                    for (int j = 0; j < messageEvent.length(); j++) {
//                        JSONObject event = messageEvent.getJSONObject(j);
//                        if (!event.isNull("optin")) {
//
//                        } else if (!event.isNull("message")) {
//                            receivedMessage(event);
//                        } else if (!event.isNull("delivery")) {
//
//                        } else if (!event.isNull("postback")) {
//                            receivedPostback(event);
//                        } else {
//                            System.out.println();
//                        }
//                    }
//                }
//            }
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    public void webhookSetupDetails(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getQueryString());
        String queryString = request.getQueryString();
        if (queryString == null) {
            response.setContentType("application/json");
            try (PrintWriter out = response.getWriter()) {
                /* TODO output your page here. You may use following sample code. */
                out.println("{}");
            } catch (IOException ex) {
                Logger.getLogger(Webhook.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            String tokens[] = queryString.split("&");
            String mode = tokens[0].split("=")[1];
            String challenge = tokens[1].split("=")[1];
            String verify_token = tokens[2].split("=")[1];

            if (mode.equalsIgnoreCase("subscribe") && verify_token.equalsIgnoreCase("this_is_a_testing_verification_token")) {
                response.setContentType("application/json");
                try (PrintWriter out = response.getWriter()) {
                    /* TODO output your page here. You may use following sample code. */
                    out.println(challenge);
                } catch (IOException ex) {
                    Logger.getLogger(Webhook.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                response.setContentType("application/json");
                try (PrintWriter out = response.getWriter()) {
                    /* TODO output your page here. You may use following sample code. */
                    out.println("Invalid Access Token");
                } catch (IOException ex) {
                    Logger.getLogger(Webhook.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    private void receivedMessage(Messaging event) {
        String senderID = event.getSender().getId();
        String recipientID = event.getRecipient().getId();

        long timeOfMessage = event.getTimestamp();
        Message message = event.getMessage();

        System.out.println("Received message for user " + senderID + " and page " + recipientID + " at " + timeOfMessage + " with\n\n message: " + gson.toJson(message) + "\n\n\n");

        String messageId = message.getMid();

        String messageText = null;
        Attachments messageAttachments;
        if (!message.getIsEcho()) {

            if (message.getText() != null) {
                messageText = message.getText();
                PagesInsertData page = getStoreName(recipientID);
                if (AppConstant.messages.contains(messageText.toLowerCase(Locale.ENGLISH))) {
                    sendTextMessage(senderID, "Hello, " + page.getName() + " welcomes you", page);
                    sendTextMessage(senderID, "Choose a collection:- ", page);
                    sendCollection(senderID, getCollectionList(), page);
//                    sendCalculator(senderID, messageText);
                } else {
                    sendTextMessage(senderID, "Please use either hi or hello to start conversation!! :)", page);
                }
            } else if (message.getAttachments() != null) {
                sendTextMessage(senderID, "attachment received", getStoreName(recipientID));
                System.out.println(gson.toJson(message.getAttachments()));
            } else {
                sendTextMessage(senderID, "Something went wrong", getStoreName(recipientID));
            }
//        if (!message.isNull("attachments")) {
//            messageAttachments = message.getString("attachments");
//        }

//        ScriptEngineManager mgr = new ScriptEngineManager();
//        ScriptEngine engine = mgr.getEngineByName("JavaScript");
//        Object total = null;
//        try {
//            System.out.println(engine.eval(messageText));
//            total = engine.eval(messageText);
//        } catch (ScriptException ex) {
//            Logger.getLogger(Webhook.class.getName()).log(Level.SEVERE, null, ex);
//        }
//            if (messageText != null) {
//
//            } else {
//            if (!message.isNull("attachments")) {
//                messageAttachments = message.getJSONObject("attachments");
//            } else {
//                sendTextMessage(senderID, "Something went wrong");
//            }
//            }
        } else {
            System.out.println("echo received");
//            sendTextMessage(recipientID, "message received");
        }

    }

    private void sendTextMessage(String recipientID, String messageText, PagesInsertData page) {

        TextMessageResponse messageData = new TextMessageResponse(new Recipient(recipientID), new SendMessage(messageText));

//        String messageData = "{recipient:{id:" + recipientID + "}, message:{text:" + messageText + "}}";
//        JSONObject messageData = new JSONObject();
//        JSONObject message = new JSONObject();
//        message.put("text", messageText);
//        messageData.put("recipient", recipient);
//        messageData.put("message", message);
        System.out.println(gson.toJson(messageData));

        AppConstant.ACCESSTOKEN = page.getAccessToken();

        if (!AppConstant.ACCESSTOKEN.isEmpty()) {

            RestClient.instance.getApiService().sendTextMessage(messageData, new Callback<Response>() {

                @Override
                public void failure(RetrofitError re) {
                    System.err.println("something went wrong on facebook response");
                }

                @Override
                public void success(Response result, Response rspns) {
                    System.out.println("facebook response: " + gson.toJson(rspns));
                }
            });
        } else {
            System.out.println("ACCESS TOKEN should not be null");
        }

    }

    public ArrayList<Collection> getCollectionList() {

        MongoDatabase db = MongoProvider.getInstance();

//        MongoCollection<Product> products = db.getCollection("disneytoys.myshopify.com_products", Product.class);
        BasicDBObject document = new BasicDBObject();
        QueryBuilder qb = new QueryBuilder();
        qb.or(new QueryBuilder().put("handle").notEquals("frontpage").get(), new QueryBuilder().put("published_at").notEquals(null).get());
        document.putAll(qb.get());

        FindIterable<Document> coll = db.getCollection("dakshal.myshopify.com_collections").find(and(ne("title", "Frontpage"), ne("published_at", null)));
//        FindIterable<Document> coll = db.getCollection("disneytoys.myshopify.com_collection").find(document);

//        FindIterable<Document> foundDocument = coll.find(and(ne("title", "Frontpage"), ne("published_at", null)));
        final Type collectionType = new TypeToken<ArrayList<Collection>>() {
        }.getType();

        final ArrayList<Collection> collection = new ArrayList<>();

        System.out.println();
        System.out.println();

        System.out.println("document: " + document);
        coll.forEach(new Block<Document>() {
            @Override
            public void apply(Document document) {

                collection.add(gson.fromJson(gson.toJson(document), Collection.class));

//                System.out.println("document: " + document);
//                ArrayList<Collection> c = gson.fromJson(gson.toJson(document.get("collections")), collectionType);
//                System.out.println("collection: " + c);
//                if (!c.isEmpty()) {
//                    collection.addAll(c);
//                }
//                collection.add(t);
            }
        });
        System.out.println("Collection: " + gson.toJson(collection));
        System.out.println();
        System.out.println();

        return collection;
    }

    private void receivedPostback(Messaging event) {
        String senderID = event.getSender().getId();
        String recipientID = event.getRecipient().getId();

        long timeOfPostBack = event.getTimestamp();
        String postbackMessage = event.getPostback().getPayload();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(postbackMessage);
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        PagesInsertData page = getStoreName(recipientID);

        ResponseModel response = gson.fromJson(postbackMessage, ResponseModel.class);

        System.out.println(gson.toJson(response));

        if (response.getLength() == 0) {
            sendCollection(senderID, getCollectionList(), page);
        } else if (response.getLength() == 1) {
            sendProductList(senderID, getProducts(response.getCollectionID()), response, page);
        } else if (response.getLength() >= 2) {
            sendVarientList(senderID, getVarients(response.getProductID()), response.getLength(), response, page);
        }

//        if (postbackMessage.contains("collectionId")) {
//            sendProductList(senderID, getProducts(postbackMessage.split(":")[1]), postbackMessage.split(":")[1], page);
////            sendTextMessage(senderID, "Postback was Called with: "+ postbackMessage);
//        } else if (postbackMessage.contains("productID")) {
//            sendVarientList(senderID, getVarients(postbackMessage.split(":")[1].split("@")[1]), 1, postbackMessage.split(":")[1], page);
////            sendTextMessage(senderID, "Postback was Called with: "+ postbackMessage);
//        } else if (postbackMessage.contains("varient")) {
//            sendVarientList(senderID, getVarients(postbackMessage.split(":")[1].split("@")[1]), postbackMessage.split(":")[1].split("@").length - 1, postbackMessage.split(":")[1], page);
////            sendTextMessage(senderID, "Postback was Called with: "+ postbackMessage);
//        } else if (postbackMessage.contains("back")) {
//
//            String[] choises = postbackMessage.split(":");
//
//            System.out.println(gson.toJson(choises));
//
//            if (choises[1].split("@").length == 1) {
//                sendCollection(senderID, getCollectionList(), page);
//            } else if (choises[1].split("@").length == 2) {
//                sendProductList(senderID, getProducts(choises[1].split("@")[0]), choises[1].split("@")[0], page);
//            } else {
//                String[] selectedCategories = choises[1].split("@");
//                String items = "";
//                for (int i = 0; i < selectedCategories.length - 1; i++) {
//                    items = items + selectedCategories[i] + "@";
//                }
//
//                items = items.substring(0, items.length() - 1);
//                System.out.println("items: -----> " + gson.toJson(items));
//
//                sendVarientList(senderID, getVarients(items.split("@")[1]), items.split("@").length - 1, items, page);
//            }
////            sendVarientList(senderID, getVarients(choises[1].split("@")[1]), choises[1].split("@").length - 1, choises[1]);
////            sendTextMessage(senderID, "Postback was Called with: "+ postbackMessage);
//
//        } else if (postbackMessage.contains("home")) {
//
//            sendCollection(senderID, getCollectionList(), page);
//
//        } else {
//            sendTextMessage(senderID, "Postback was Called", page);
//        }
//        if (postbackMessage.contains("buy")) {
//            sendTextMessage(senderID, "Enter your address for delivery:");
//        } else {
//            sendTextMessage(senderID, "Postback was Called");
//        }
    }
//    private void sendOrderDetails(String recipientID, String textMessage) {
//        JSONObject messageData = new JSONObject();
//        JSONObject recipient = new JSONObject();
//        recipient.put("id", recipientID);
//
//        JSONObject message = new JSONObject();
//
//        JSONObject attachment = new JSONObject();
//        attachment.put("type", "template");
//        JSONObject payload = new JSONObject();
//        payload.put("template_type", "receipt");
//        payload.put("recipient_name", "Amrish Patel");
//        payload.put("order_number", recipientID);
//        payload.put("currency", "INR");
//        payload.put("payment_method", "Visa 1234");
//        payload.put("timestamp", "1428444852");
//
//        JSONArray elements = new JSONArray();
//
////        for (int i = 0; i < 5; i++) {
//        JSONObject elementObject = new JSONObject();
//        elementObject.put("title", elementTitle[2]);
//        elementObject.put("subtitle", elementSubTitle[2]);
//        elementObject.put("quantity", 1);
//        elementObject.put("price", 100);
//        elementObject.put("currency", "INR");
//        elementObject.put("image_url", elementImageURL[2]);
//
////            elementObject.put("buttons", buttons);
//        elements.put(elementObject);
////        }
//
//        textMessage = textMessage.split(":")[1];
//
//        JSONObject street = new JSONObject();
//
//        street.put("street_1", textMessage);
//        street.put("street_2", "");
//        street.put("city", "Ahmedabad");
//        street.put("postal_code", "380028");
//        street.put("state", "gujarat");
//        street.put("country", "IN");
//
//        payload.put("address", street);
//
//        payload.put("summary", "{'subtotal': 100.00,'shipping_cost': 100.00,'total_tax': 28.00,'total_cost': 228.00}");
//
//        payload.put("elements", elements);
//
//        attachment.put("payload", payload);
//
//        message.put("attachment", attachment);
//
////        message.put("text", messageText);
//        messageData.put("recipient", recipient);
//        messageData.put("message", message);
//
//        System.out.println(messageData.toString());
//
//        try {
//            callSendApi(messageData.toString());
//        } catch (Exception ex) {
//            Logger.getLogger(Webhook.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    private void sendTextMessageToSensex(String recipientID, String messageText) {
//        JSONObject messageData = new JSONObject();
//        JSONObject recipient = new JSONObject();
//        recipient.put("id", recipientID);
//
//        JSONObject message = new JSONObject();
//        message.put("text", messageText);
//
//        messageData.put("recipient", recipient);
//        messageData.put("message", message);
//
//        System.out.println(messageData.toString());
//
//        try {
//            callSensexApi(messageData.toString());
//        } catch (Exception ex) {
//            Logger.getLogger(Webhook.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    private void callSensexApi(String messageData) throws Exception {
////        String url = "https://requestb.in/1g3689z1";
//        String url = "https://graph.facebook.com/v2.6/me/messages";
////        String url = "https://selfsolve.apple.com/wcResults.do";
//        String access_token = "?access_token=EAAYvnguYXe4BAP6jDZBo3eRyL43Aki7o6WBd7jLFsBP6RZALxr5PjQZBWtHNNwDti9aErga4pRdVaKlaZAwZCVAlvFeXXdURAqnZCFFWn9tD6Al0RYFTP0il3a8G2OOpUiHHYIb1ntZCYsAsIva1R46kRoIb5NchVv33EmQZBMdjZCwZDZD";
//
//        HttpClient client = HttpClientBuilder.create().build();
//        url = url + access_token;
//        HttpPost post = new HttpPost(url);
//
//        // add header
//        post.setEntity(new StringEntity(messageData, "UTF8"));
//        post.setHeader("Content-type", "application/json");
////	post.setHeader("User-Agent", USER_AGENT);
////        post.setHeader("qs", access_token);
////        post.setHeader("json", messageData);
//
////	List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
////	urlParameters.add(new BasicNameValuePair("qs", "{access_token: EAAYvnguYXe4BADHGRQJE3iVNHvRqqFwYCTbLS5XvjEbZCf7PkP1eSqQpPd7yebCZCUuHtZARk6bDJWvnqZCnaMYaZBe8YIFQK2580vwX0xtm45ZBrYs1Y7N8eiFgGZCGqHj6vDEeZB2ah3xZBWTTszPyiSdEQ19WrOlv7caaql7ZCsUQZDZD}"));
////	urlParameters.add(new BasicNameValuePair("json", messageData));
////	urlParameters.add(new BasicNameValuePair("locale", ""));
////	urlParameters.add(new BasicNameValuePair("caller", ""));
////	urlParameters.add(new BasicNameValuePair("num", "12345"));
////
////	post.setEntity((HttpEntity) urlParameters);
//        HttpResponse response = client.execute(post);
//
//        System.out.println("Response Code : "
//                + response.getStatusLine().getStatusCode() + "\t\t message: " + messageData);
//
//        BufferedReader rd = new BufferedReader(
//                new InputStreamReader(response.getEntity().getContent()));
//
//        StringBuffer result = new StringBuffer();
//        String line = "";
//        while ((line = rd.readLine()) != null) {
//            result.append(line);
//        }
//    }

    private void sendCollection(String recepientID, ArrayList<Collection> collection, PagesInsertData page) {

        SendAttachments attachment = new SendAttachments();

        attachment.setType("template");

        SendPayload payload = new SendPayload();

        ArrayList<Element> elements = new ArrayList<Element>();

        payload.setTemplateType("generic");

        for (int i = 0; i < collection.size(); i++) {

            if (collection.get(i).getPublishedAt() != null) {

                ArrayList<Button> button = new ArrayList<Button>();
                button.add(new PayloadButton("postback", "select " + collection.get(i).getTitle(), gson.toJson(new ResponseModel(collection.get(i).getId(), 1))));
                button.add(new RedirectButton("web_url", "visit website", "https://www.mlveda.com"));

                Element element = new Element();
                if (collection.get(i).getImage() != null) {
                    element.setImage_url(collection.get(i).getImage().getSrc());
                }
                element.setButtons(button);
                element.setTitle(collection.get(i).getTitle());
                element.setSubtitle(collection.get(i).getBodyHtml());

                elements.add(element);

            }
        }

        payload.setElements(elements);

        attachment.setPayload(payload);

        System.out.println("message generated");

        SendAttachmentMessage messageData = new SendAttachmentMessage(new Recipient(recepientID), new SendAttachment(attachment));

        System.out.println(gson.toJson(messageData));

        AppConstant.ACCESSTOKEN = page.getAccessToken();

        if (!AppConstant.ACCESSTOKEN.isEmpty()) {

            RestClient.instance.getApiService().sendAttachmentToUser(messageData, new Callback<Response>() {

                @Override
                public void failure(RetrofitError re) {
                    System.err.println("something went wrong on facebook response");
                }

                @Override
                public void success(Response response, Response rspns) {
                    System.out.println("facebook response: " + gson.toJson(rspns));
                }
            });
        } else {
            System.out.println("ACCESS TOKEN should not be null");
        }

    }

    private void sendProductList(String recepientID, ArrayList<Product> products, ResponseModel response, PagesInsertData page) {

        SendAttachments attachment = new SendAttachments();

        attachment.setType("template");

        SendPayload payload = new SendPayload();

        ArrayList<Element> elements = new ArrayList<Element>();

        payload.setTemplateType("generic");

        System.out.println("products: " + products.size());

        for (Product prod : products) {

            if (prod.getPublishedAt() != null) {

                System.out.println("product is published");

                ArrayList<Button> button = new ArrayList<Button>();
                response.setProductID(prod.getId());
                response.setLength(2);
                button.add(new PayloadButton("postback", "select " + prod.getTitle(), gson.toJson(response)));
                response.setLength(0);
                button.add(new PayloadButton("postback", "back", gson.toJson(response)));
                button.add(new PayloadButton("postback", "home", gson.toJson(new ResponseModel(0, 0))));
//                button.add(new RedirectButton("postback", "back", "back"));

                Element element = new Element();
                if (prod.getImages().size() > 0) {
                    element.setImage_url(prod.getImages().get(0).getSrc());
                }
                element.setButtons(button);
                element.setTitle(prod.getTitle());
                element.setSubtitle(prod.getBodyHtml());

                elements.add(element);

            }
        }

        payload.setElements(elements);

        attachment.setPayload(payload);

        System.out.println("message generated");

        SendAttachmentMessage messageData = new SendAttachmentMessage(new Recipient(recepientID), new SendAttachment(attachment));

        System.out.println(gson.toJson(messageData));

        if (elements.size() == 0) {

            sendTextMessage(recepientID, "No Products to show", page);

        } else {

            AppConstant.ACCESSTOKEN = page.getAccessToken();

            if (!AppConstant.ACCESSTOKEN.isEmpty()) {

                RestClient.instance.getApiService().sendAttachmentToUser(messageData, new Callback<Response>() {

                    @Override
                    public void failure(RetrofitError re) {
                        System.err.println("something went wrong on facebook response");
                    }

                    @Override
                    public void success(Response response, Response rspns) {
                        System.out.println("facebook response: " + gson.toJson(rspns));
                    }
                });

            } else {
                System.out.println("ACCESS TOKEN should not be null");
            }
        }

    }

    private void sendVarientList(String recepientID, Product product, int position, ResponseModel otherParams, PagesInsertData page) {

        SendAttachments attachment = new SendAttachments();

        attachment.setType("template");

        SendPayload payload = new SendPayload();

        ArrayList<Element> elements = new ArrayList<Element>();

        payload.setTemplateType("generic");

//        System.out.println("products: " + products.size());
        System.out.println("product is published");

        System.out.println("option size: " + product.getOptions().size() + "position: ----------->" + position);

        if (product.getOptions().size() + 2 <= position) {

//            sendTextMessage(recepientID, "No more varients to show", page);
            MongoDatabase db = MongoProvider.getInstance();

//            String ids[] = otherParams.split("@");
            int id = 0;
            System.out.println("collectionID: " + otherParams.getCollectionID() + "other params:----> " + gson.toJson(otherParams));

            FindIterable<Document> coll = db.getCollection("dakshal.myshopify.com_collections").find(eq("id", otherParams.getCollectionID()));

            System.out.println();
            System.out.println();
            System.out.println("collection size:-> " + gson.toJson(coll.first()));
            System.out.println();
            System.out.println();

            Collection collection = gson.fromJson(gson.toJson(coll.first()), Collection.class);

            OUTER:
            for (id = 0; id < product.getVariants().size(); id++) {
                switch (position) {
                    case 3:
                        if (product.getVariants().get(id).getOption1().equalsIgnoreCase(otherParams.getOption1())) {
                            System.out.println("id:----->>> " + id);
                            break OUTER;
                        }
                        break;
                    case 4:
                        if (product.getVariants().get(id).getOption1().equalsIgnoreCase(otherParams.getOption1()) && product.getVariants().get(id).getOption2().equalsIgnoreCase(otherParams.getOption2())) {
                            System.out.println("id:----->>> " + id);
                            break OUTER;
                        }
                        break;
                    case 5:
                        if (product.getVariants().get(id).getOption1().equalsIgnoreCase(otherParams.getOption1()) && product.getVariants().get(id).getOption2().equalsIgnoreCase(otherParams.getOption2()) && product.getVariants().get(id).getOption3().equalsIgnoreCase(otherParams.getOption3())) {
                            System.out.println("id:----->>> " + id);
                            break OUTER;
                        }
                        break;
                }
            }

            if (id < product.getVariants().size()) {
                System.out.println("collection:----->>> " + gson.toJson(collection));
                System.out.println("page:----->>> " + gson.toJson(page));
                otherParams.setLength(otherParams.getLength() - 1);

//            switch (otherParams.getLength()) {
//                case 1:
//                    otherParams.setOption1(null);
//                    break;
//                case 2:
//                    otherParams.setOption2(null);
//                    otherParams.setLength(otherParams.getLength() - 1);
//                    break;
//                case 3:
//                    otherParams.setOption3(null);
//                    otherParams.setLength(otherParams.getLength() - 1);
//                    break;
//            }
                ArrayList<Button> button = new ArrayList<>();
//                button.add(new RedirectButton("web_url", "Buy @ ₹ " + product.getVariants().get(id).getPrice(), "http://" + page.getShopName() + ".myshopify.com/collections/" + collection.getHandle() + "/products/" + product.getHandle() + "?variant=" + product.getVariants().get(id).getId()));
                button.add(new RedirectButton("web_url", "Buy @ ₹ " + product.getVariants().get(id).getPrice(), "http://" + page.getShopName() + ".myshopify.com/cart/" + product.getVariants().get(id).getId() + ":1"));
                button.add(new PayloadButton("postback", "back", gson.toJson(otherParams)));
                button.add(new PayloadButton("postback", "home", gson.toJson(new ResponseModel(0, 0))));
                Element element = new Element();
                Variant variant = product.getVariants().get(id);
                if (product.getImages().size() > 0) {
                    if (variant.getImageId() != 0) {
                        for (Image images : product.getImages()) {
                            if (images.getId() == variant.getImageId()) {
                                element.setImage_url(images.getSrc());
//                                            imageURL.add(images.getSrc());
                                break;
                            }
                        }
                    } else {
                        element.setImage_url(product.getImages().get(0).getSrc());
//                                    imageURL.add(product.getImages().get(0).getSrc());
                    }
                } else {
//                    element.setImage_url(product.getImages().get(0).getSrc());
//                                imageURL.add(null);
                }
//                if (product.getImages().size() > 0) {
//                    element.setImage_url(product.getImages().get(0).getSrc());
//                }
                element.setButtons(button);
                element.setTitle(product.getTitle());
                element.setSubtitle("");

                elements.add(element);

                payload.setElements(elements);

                attachment.setPayload(payload);

                System.out.println("message generated");

                SendAttachmentMessage messageData = new SendAttachmentMessage(new Recipient(recepientID), new SendAttachment(attachment));

                System.out.println(gson.toJson(messageData));

                AppConstant.ACCESSTOKEN = page.getAccessToken();

                if (!AppConstant.ACCESSTOKEN.isEmpty()) {
                    RestClient.instance.getApiService().sendAttachmentToUser(messageData, new Callback<Response>() {

                        @Override
                        public void failure(RetrofitError re) {
                            System.err.println("something went wrong on facebook response");
                        }

                        @Override
                        public void success(Response response, Response rspns) {
                            System.out.println("facebook response: " + gson.toJson(rspns));
                        }
                    });
                } else {
                    System.out.println("ACCESS TOKEN should not be null");
                }
            } else {
                sendTextMessage(recepientID, "something went wrong!!! Please try again.:)", page);
            }
//            sendTextMessage(recepientID, otherParams, page);
        } else {

            ArrayList<String> options = new ArrayList<>();
            ArrayList<String> imageURL = new ArrayList<>();

            switch (position) {
                case 2:
                    for (Variant variant : product.getVariants()) {
                        if (!options.contains(variant.getOption1())) {
                            options.add(variant.getOption1());
                            if (product.getImages().size() > 0) {
                                if (variant.getImageId() != 0) {
                                    for (Image images : product.getImages()) {
                                        if (images.getId() == variant.getImageId()) {
                                            imageURL.add(images.getSrc());
                                            break;
                                        }
                                    }
                                } else {
                                    imageURL.add(product.getImages().get(0).getSrc());
                                }
                            } else {
                                imageURL.add(null);
                            }
                        }
                    }
                    break;
                case 3: {
                    String option1 = otherParams.getOption1();
                    for (Variant variant : product.getVariants()) {
                        if (!options.contains(variant.getOption2()) && variant.getOption1().equalsIgnoreCase(option1)) {
                            options.add(variant.getOption2());
                            System.out.println();
                            System.out.println();
                            System.out.println(gson.toJson(variant));
                            System.out.println();
                            System.out.println();
                            if (product.getImages().size() > 0) {
                                if (variant.getImageId() != 0) {
                                    for (Image images : product.getImages()) {
                                        if (images.getId() == variant.getImageId()) {
                                            imageURL.add(images.getSrc());
                                            break;
                                        }
                                    }
                                } else {
                                    imageURL.add(product.getImages().get(0).getSrc());
                                }
                            } else {
                                imageURL.add(null);
                            }
                        }
                    }
                    break;
                }
                case 4: {
                    String option1 = otherParams.getOption1();
                    String option2 = otherParams.getOption2();
                    for (Variant variant : product.getVariants()) {
                        if (!options.contains(variant.getOption3()) && variant.getOption1().equals(option1) && variant.getOption2().equals(option2)) {
                            options.add(variant.getOption3());
                            if (product.getImages().size() > 0) {
                                if (variant.getImageId() != 0) {
                                    for (Image images : product.getImages()) {
                                        if (images.getId() == variant.getImageId()) {
                                            imageURL.add(images.getSrc());
                                            break;
                                        }
                                    }
                                } else {
                                    imageURL.add(product.getImages().get(0).getSrc());
                                }
                            } else {
                                imageURL.add(null);
                            }
                        }
                    }
                    break;
                }
                default:
                    break;
            }

//            for (Option option : product.getOptions()) {
//                System.out.println("position : " + option.getPosition() + " selected: " + position);
//                if (option.getPosition() == position - 1) {
            System.out.println("varient size: " + gson.toJson(options));
            for (int i = 0; i < options.size(); i++) {
                String value = options.get(i);
                switch (position - 1) {
                    case 1:
                        otherParams.setOption1(value);
                        break;
                    case 2:
                        otherParams.setOption2(value);
                        break;
                    case 3:
                        otherParams.setOption3(value);
                        break;
                }
                otherParams.setLength(position + 1);
                ArrayList<Button> button = new ArrayList<>();
                button.add(new PayloadButton("postback", "" + value, gson.toJson(otherParams)));
//                        switch (otherParams.getLength()) {
//                            case 1:
//                                otherParams.setOption1(null);
//                                otherParams.setProductID(0);
//                                break;
//                            case 2:
//                                otherParams.setOption1(null);
//                                otherParams.setOption2(null);
//                                otherParams.setLength(otherParams.getLength() - 1);
//                                break;
//                            case 3:
//                                otherParams.setOption2(null);
//                                otherParams.setOption3(null);
//                                otherParams.setLength(otherParams.getLength() - 1);
//                                break;
//                        }
//                    button.add(new RedirectButton("postback", "back", "back:" + otherParams));
                otherParams.setLength(position - 1);
                button.add(new PayloadButton("postback", "back", gson.toJson(otherParams)));
                button.add(new PayloadButton("postback", "home", gson.toJson(new ResponseModel(0, 0))));
                Element element = new Element();
//                        if (product.getImages().size() > 0) {
//                            element.setImage_url(product.getImages().get(0).getSrc());
//                        }
                if (imageURL.get(i) != null) {
                    element.setImage_url(imageURL.get(i));
                }
                element.setButtons(button);
                element.setTitle("Select " + product.getOptions().get(position - 2).getName());
                element.setSubtitle(product.getTitle());

                elements.add(element);

            }
//                    otherParams.setLength(position - 1);
//                    button.add(new PayloadButton("postback", "back", gson.toJson(otherParams)));
//                    button.add(new PayloadButton("postback", "home", gson.toJson(new ResponseModel(0, 0))));
//                    Element element = new Element();
//                    if (product.getImages().size() > 0) {
//                        element.setImage_url(product.getImages().get(0).getSrc());
//                    }
//                    element.setButtons(button);
//                    element.setTitle("Select " + product.getOptions().get(position - 1).getName());
//                    element.setSubtitle(product.getTitle());
//
//                    elements.add(element);
//                    break;
//                }
//            }

            payload.setElements(elements);

            attachment.setPayload(payload);

            System.out.println("message generated");

            SendAttachmentMessage messageData = new SendAttachmentMessage(new Recipient(recepientID), new SendAttachment(attachment));

            System.out.println(gson.toJson(messageData));

            AppConstant.ACCESSTOKEN = page.getAccessToken();

            if (!AppConstant.ACCESSTOKEN.isEmpty()) {
                RestClient.instance.getApiService().sendAttachmentToUser(messageData, new Callback<Response>() {

                    @Override
                    public void failure(RetrofitError re) {
                        System.err.println("something went wrong on facebook response");
                    }

                    @Override
                    public void success(Response response, Response rspns) {
                        System.out.println("facebook response: " + gson.toJson(rspns));
                    }
                });
            } else {
                System.out.println("ACCESS TOKEN should not be null");
            }
        }
    }

    private ArrayList<Product> getProducts(long collectionID) {
        MongoDatabase db = MongoProvider.getInstance();

//        MongoCollection<Product> products = db.getCollection("disneytoys.myshopify.com_products", Product.class);
        System.out.println("collectionID: " + collectionID);

        FindIterable<Document> coll = db.getCollection("dakshal.myshopify.com_collects").find(eq("collection_id", collectionID));

        final Type collectionType = new TypeToken<ArrayList<ProductCollectionMapping>>() {
        }.getType();
        final Type productType = new TypeToken<ArrayList<Product>>() {
        }.getType();

        final ArrayList<ProductCollectionMapping> map = new ArrayList<>();
        final ArrayList<Product> products = new ArrayList<>();

        System.out.println();
        System.out.println("coll: " + coll.first());
        System.out.println();

        coll.forEach(new Block<Document>() {
            @Override
            public void apply(Document document) {
                map.add(gson.fromJson(gson.toJson(document), ProductCollectionMapping.class
                ));
//                System.out.println("document is: " + gson.toJson(document));
//                ArrayList<ProductCollectionMapping> c = gson.fromJson(gson.toJson(document.get("collects")), collectionType);
//                System.out.println(c);
//                map.addAll(c);
//                collection.add(t);
            }
        });

        System.out.println();
        System.out.println("map: " + map.size());
        System.out.println();

        for (ProductCollectionMapping pcm : map) {
            System.out.println("productID: " + pcm.getProductId());
            FindIterable<Document> prod = db.getCollection("dakshal.myshopify.com_products").find(eq("id", pcm.getProductId()));
            System.out.println();
            System.out.println("prod: " + prod.toString());
            System.out.println();
            prod.forEach(new Block<Document>() {
                @Override
                public void apply(Document document) {
                    Product prod = gson.fromJson(gson.toJson(document), Product.class);
                    if (!products.contains(prod)) {
                        products.add(prod);
                    }
//                    System.out.println(gson.toJson(document));
//                    ArrayList<Product> c = gson.fromJson(gson.toJson(document.get("collects")), productType);
//                    System.out.println(c);
//                    products.addAll(c);
//                collection.add(t);
                }
            });
        }

        System.out.println();
        System.out.println();

        System.out.println("productlist: " + gson.toJson(products));

        return products;
    }

    private Product getVarients(long productID) {
        MongoDatabase db = MongoProvider.getInstance();

//        MongoCollection<Product> products = db.getCollection("disneytoys.myshopify.com_products", Product.class);
        System.out.println("collectionID: " + productID);

        System.out.println("productID: " + productID);
        FindIterable<Document> prod = db.getCollection("dakshal.myshopify.com_products").find(eq("id", productID));
        System.out.println();
        System.out.println("prod: " + prod.toString());
        System.out.println();

        Product product = gson.fromJson(gson.toJson(prod.first()), Product.class);

//        prod.forEach(new Block<Document>() {
//            @Override
//            public void apply(Document document) {
//                Product prod = gson.fromJson(gson.toJson(document), Product.class
//                );
//                if (!products.contains(prod)) {
//                    products.add(prod);
//                }
////                    System.out.println(gson.toJson(document));
////                    ArrayList<Product> c = gson.fromJson(gson.toJson(document.get("collects")), productType);
////                    System.out.println(c);
////                    products.addAll(c);
////                collection.add(t);
//            }
//        });
        System.out.println();
        System.out.println();

        System.out.println("productlist: " + gson.toJson(product));

        return product;
    }

    private PagesInsertData getStoreName(String storeId) {
        MongoDatabase db = MongoProvider.getInstance();

        FindIterable<Document> collection = db.getCollection("facebook_pages").find(eq("id", storeId));

        PagesInsertData facebookPage = gson.fromJson(gson.toJson(collection.first()), PagesInsertData.class);

        return facebookPage;
    }
//
//    private void sendTextMessages(String recipientID, String messageText, PagesInsertData page) {
//
//        TextMessageResponse messageData = new TextMessageResponse(new Recipient(recipientID), new SendMessage(messageText));
//
////        String messageData = "{recipient:{id:" + recipientID + "}, message:{text:" + messageText + "}}";
////        JSONObject messageData = new JSONObject();
////        JSONObject message = new JSONObject();
////        message.put("text", messageText);
////        messageData.put("recipient", recipient);
////        messageData.put("message", message);
//        System.out.println(gson.toJson(messageData));
//
//        AppConstant.ACCESSTOKEN = page.getAccessToken();
//
//        if (!AppConstant.ACCESSTOKEN.isEmpty()) {
//            RestClient.instance.getApiService().sendTextMessageToUser(messageData, new Callback<Response>() {
//
//                @Override
//                public void failure(RetrofitError re) {
//                    System.err.println("something went wrong on facebook response");
//                }
//
//                @Override
//                public void success(Response result, Response rspns) {
//                    System.out.println("facebook response: " + gson.toJson(rspns));
//                }
//            });
//        } else {
//            System.out.println("ACCESS TOKEN should not be null");
//        }
//    }
}
