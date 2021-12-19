//package com.example.movieapp.utils;
//
//import com.google.gson.Gson;
//import com.google.gson.annotations.SerializedName;
//import com.stripe.android.Stripe;
//import com.stripe.android.model.PaymentIntent;
//
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Server {
//    private static Gson gson = new Gson();
//    static class CreatePayment {
//        @SerializedName("items")
//        Object[] items;
//
//        public Object[] getItems() {
//            return items;
//        }
//    }
//
//    static class CreatePaymentResponse {
//        private String clientSecret;
//        public CreatePaymentResponse(String clientSecret) {
//            this.clientSecret = clientSecret;
//        }
//    }
//
//    static int calculateOrderAmount(Object[] items) {
//        // Replace this constant with a calculation of the order's amount
//        // Calculate the order total on the server to prevent
//        // people from directly manipulating the amount on the client
//        return 1400;
//    }
//    public static void main(String[] args) {
//        port(4242);
//        staticFiles.externalLocation(Paths.get("public").toAbsolutePath().toString());
//
//        Stripe.apiKey = "pk_test_51JwIpuIid84sdPkuFSIgkd6WU6ym7cutXYuiGCYqI9SOVA6mqHwlRDhhunuRGi1HXN9gpx6OBrcbPH87SfBG9SWt00BpzgJRkw";
//
//        post("/create-payment-intent", (request, response) -> {
//            response.type("application/json");
//
//            CreatePayment postBody = gson.fromJson(request.body(), CreatePayment.class);
//            List<String> paymentMethodTypes = new ArrayList<>();
//            paymentMethodTypes.add("sofort");
//            paymentMethodTypes.add("sepa_debit");
//            paymentMethodTypes.add("card");
//            paymentMethodTypes.add("bancontact");
//            paymentMethodTypes.add("ideal");
//            PaymentIntentCreateParams params =
//                    PaymentIntentCreateParams.builder()
//                            .setAmount(new Long(calculateOrderAmount(postBody.getItems())))
//                            .setCurrency("eur")
//                            .addAllPaymentMethodType(paymentMethodTypes)
//                            .build();
//
//            PaymentIntent paymentIntent = PaymentIntent.create(params);
//
//            CreatePaymentResponse paymentResponse = new CreatePaymentResponse(paymentIntent.getClientSecret());
//            return gson.toJson(paymentResponse);
//        });
//    }
//}
