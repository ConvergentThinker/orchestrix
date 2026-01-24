# 🌍 Real-World Use Cases & Examples

## Table of Contents
1. [E-Commerce Applications](#e-commerce-applications)
2. [Banking & Financial Apps](#banking--financial-apps)
3. [Healthcare Applications](#healthcare-applications)
4. [Social Media Apps](#social-media-apps)
5. [Food Delivery Apps](#food-delivery-apps)
6. [Travel & Booking Apps](#travel--booking-apps)
7. [Fitness & Wellness Apps](#fitness--wellness-apps)
8. [Enterprise Applications](#enterprise-applications)
9. [CI/CD Integration Scenarios](#cicd-integration-scenarios)

---

## E-Commerce Applications

### Use Case 1: Complete Shopping Journey

**Business Requirement**: Test the entire customer journey from browsing to order confirmation across multiple devices.

**Test Scenario**:
```java
@Test(description = "Complete shopping journey - Browse, Add to Cart, Checkout")
public void testCompleteShoppingJourney() {
    // 1. Login
    LoginPage loginPage = new LoginPage();
    HomePage homePage = loginPage.login("customer@shop.com", "password123");
    
    // 2. Browse Categories
    CategoryPage categoryPage = homePage.selectCategory("Electronics");
    categoryPage.filterByBrand("Apple");
    categoryPage.sortByPrice("Low to High");
    
    // 3. Product Selection
    ProductListPage productList = categoryPage.viewProducts();
    ProductDetailPage productDetail = productList.selectProduct("iPhone 15 Pro");
    
    // Verify product details
    Assert.assertEquals(productDetail.getProductName(), "iPhone 15 Pro");
    Assert.assertTrue(productDetail.getPrice() > 0);
    Assert.assertTrue(productDetail.hasProductImages());
    
    // 4. Add to Cart
    productDetail.selectColor("Space Black");
    productDetail.selectStorage("256GB");
    productDetail.addToCart();
    
    // 5. Cart Management
    CartPage cartPage = productDetail.viewCart();
    Assert.assertEquals(cartPage.getCartItemCount(), 1);
    Assert.assertTrue(cartPage.getTotalAmount() > 0);
    
    // 6. Apply Promo Code
    cartPage.applyPromoCode("SAVE20");
    Assert.assertTrue(cartPage.isPromoApplied());
    
    // 7. Checkout
    CheckoutPage checkout = cartPage.proceedToCheckout();
    checkout.enterShippingAddress(
        "123 Main Street", 
        "New York", 
        "NY", 
        "10001", 
        "United States"
    );
    checkout.selectShippingMethod("Express Delivery");
    checkout.selectPaymentMethod("Credit Card");
    checkout.enterCardDetails("4111111111111111", "12/25", "123", "John Doe");
    
    // 8. Review Order
    ReviewPage review = checkout.reviewOrder();
    Assert.assertTrue(review.isOrderSummaryCorrect());
    
    // 9. Place Order
    OrderConfirmationPage confirmation = review.placeOrder();
    Assert.assertTrue(confirmation.isOrderConfirmed());
    String orderNumber = confirmation.getOrderNumber();
    Assert.assertNotNull(orderNumber);
    
    // 10. Verify Order in History
    OrderHistoryPage history = homePage.viewOrderHistory();
    Assert.assertTrue(history.hasOrder(orderNumber));
}
```

**Parallel Execution**: Run on 3 devices simultaneously
- Premium: iPhone 15 Pro (iOS)
- Standard: Samsung Galaxy S21 (Android)
- Basic: Pixel 5 (Android)

### Use Case 2: Product Search and Comparison

**Business Requirement**: Test product search functionality and comparison feature.

```java
@Test(description = "Search products and compare")
public void testProductSearchAndComparison() {
    HomePage homePage = new HomePage();
    
    // Search
    SearchPage searchPage = homePage.search("laptop");
    Assert.assertTrue(searchPage.hasResults());
    
    // Filter results
    searchPage.filterByPriceRange(500, 1500);
    searchPage.filterByBrand("Dell", "HP", "Lenovo");
    searchPage.sortBy("Rating");
    
    // Select products for comparison
    searchPage.addToComparison(0); // First product
    searchPage.addToComparison(1); // Second product
    searchPage.addToComparison(2); // Third product
    
    // View comparison
    ComparisonPage comparison = searchPage.viewComparison();
    Assert.assertEquals(comparison.getProductCount(), 3);
    Assert.assertTrue(comparison.hasComparisonTable());
    
    // Compare features
    Map<String, List<String>> features = comparison.getFeatures();
    Assert.assertFalse(features.isEmpty());
    
    // Select best product
    ProductDetailPage bestProduct = comparison.selectBestProduct();
    Assert.assertNotNull(bestProduct);
}
```

---

## Banking & Financial Apps

### Use Case 3: Money Transfer with Security

**Business Requirement**: Test secure money transfer with multi-factor authentication.

```java
@Test(description = "Transfer money with 2FA")
public void testSecureMoneyTransfer() {
    // Login with biometric
    LoginPage loginPage = new LoginPage();
    loginPage.authenticateWithBiometric();
    DashboardPage dashboard = new DashboardPage();
    
    // Navigate to transfer
    TransferPage transferPage = dashboard.navigateToTransfer();
    
    // Setup transfer
    transferPage.selectFromAccount("Savings Account - ****1234");
    transferPage.selectToAccount("External Account");
    transferPage.enterAccountNumber("9876543210");
    transferPage.enterRoutingNumber("021000021");
    transferPage.enterAmount("1000.00");
    transferPage.enterMemo("Monthly savings transfer");
    
    // Review
    ReviewPage review = transferPage.reviewTransfer();
    Assert.assertEquals(review.getAmount(), "$1,000.00");
    Assert.assertEquals(review.getFromAccount(), "Savings Account");
    
    // Security verification
    SecurityVerificationPage security = review.proceed();
    security.enterOTP("123456"); // From SMS/Email
    security.answerSecurityQuestion("What was your first pet's name?", "Fluffy");
    
    // Confirm
    ConfirmationPage confirmation = security.confirmTransfer();
    Assert.assertTrue(confirmation.isTransferSuccessful());
    String transactionId = confirmation.getTransactionId();
    
    // Verify in transaction history
    TransactionHistoryPage history = dashboard.viewTransactions();
    TransactionDetails details = history.getTransaction(transactionId);
    Assert.assertEquals(details.getAmount(), "$1,000.00");
    Assert.assertEquals(details.getStatus(), "Completed");
}
```

### Use Case 4: Bill Payment

```java
@Test(description = "Pay utility bill")
public void testBillPayment() {
    DashboardPage dashboard = new DashboardPage();
    
    // Navigate to bill pay
    BillPayPage billPay = dashboard.navigateToBillPay();
    
    // Add payee
    billPay.addNewPayee("Electric Company", "123456789", "Utility");
    
    // Schedule payment
    billPay.selectPayee("Electric Company");
    billPay.enterAmount("150.00");
    billPay.selectPaymentDate("2026-02-15");
    billPay.selectPaymentMethod("Checking Account");
    
    // Review and pay
    ReviewPage review = billPay.reviewPayment();
    ConfirmationPage confirmation = review.confirmPayment();
    
    Assert.assertTrue(confirmation.isPaymentScheduled());
    String paymentId = confirmation.getPaymentId();
    
    // Verify scheduled payment
    ScheduledPaymentsPage scheduled = dashboard.viewScheduledPayments();
    Assert.assertTrue(scheduled.hasPayment(paymentId));
}
```

---

## Healthcare Applications

### Use Case 5: Telemedicine Appointment

**Business Requirement**: Test booking a video consultation with a doctor.

```java
@Test(description = "Book telemedicine appointment")
public void testTelemedicineAppointment() {
    // Login
    LoginPage loginPage = new LoginPage();
    HomePage homePage = loginPage.login("patient@health.com", "password");
    
    // Find doctor
    DoctorSearchPage search = homePage.findDoctor();
    search.searchBySpecialty("Cardiology");
    search.filterByAvailability("Today");
    search.filterByConsultationType("Video");
    
    // Select doctor
    DoctorProfilePage doctor = search.selectDoctor("Dr. Sarah Johnson");
    Assert.assertTrue(doctor.hasVideoConsultationAvailable());
    Assert.assertTrue(doctor.hasGoodRatings());
    
    // Book appointment
    AppointmentBookingPage booking = doctor.bookAppointment();
    booking.selectTimeSlot("2:00 PM - 2:30 PM");
    booking.enterSymptoms("Chest pain and shortness of breath");
    booking.uploadMedicalRecords("/path/to/records.pdf");
    booking.addInsurance("Blue Cross Blue Shield");
    
    // Review and confirm
    ReviewPage review = booking.reviewAppointment();
    Assert.assertEquals(review.getDoctorName(), "Dr. Sarah Johnson");
    Assert.assertEquals(review.getConsultationType(), "Video");
    
    ConfirmationPage confirmation = review.confirmAppointment();
    Assert.assertTrue(confirmation.isAppointmentConfirmed());
    
    // Verify calendar
    CalendarPage calendar = homePage.viewCalendar();
    Assert.assertTrue(calendar.hasAppointment("Dr. Sarah Johnson", "2:00 PM"));
}
```

### Use Case 6: Prescription Refill

```java
@Test(description = "Refill prescription")
public void testPrescriptionRefill() {
    HomePage homePage = new HomePage();
    
    // Navigate to prescriptions
    PrescriptionsPage prescriptions = homePage.viewPrescriptions();
    
    // Select prescription to refill
    PrescriptionDetailPage detail = prescriptions.selectPrescription("Lisinopril 10mg");
    Assert.assertTrue(detail.isRefillable());
    
    // Request refill
    RefillRequestPage refill = detail.requestRefill();
    refill.selectPharmacy("CVS Pharmacy - 123 Main St");
    refill.selectPickupDate("2026-02-16");
    refill.addNotes("Please call when ready");
    
    // Submit
    ConfirmationPage confirmation = refill.submitRefill();
    Assert.assertTrue(confirmation.isRefillRequested());
    
    // Track status
    PrescriptionStatusPage status = homePage.viewPrescriptionStatus();
    Assert.assertEquals(status.getStatus("Lisinopril"), "Processing");
}
```

---

## Social Media Apps

### Use Case 7: Create and Share Content

**Business Requirement**: Test content creation and sharing workflow.

```java
@Test(description = "Create post with media and share")
public void testCreateAndSharePost() {
    FeedPage feedPage = new FeedPage();
    
    // Create post
    CreatePostPage createPost = feedPage.tapCreatePost();
    
    // Add content
    createPost.enterText("Just finished an amazing workout! 💪 #fitness #motivation");
    createPost.addPhoto("/path/to/workout-photo.jpg");
    createPost.addVideo("/path/to/workout-video.mp4");
    createPost.addLocation("Central Park, New York");
    createPost.tagFriends("friend1", "friend2");
    createPost.addHashtags("#fitness #motivation #workout");
    
    // Apply filters
    createPost.applyFilter("Vibrant");
    createPost.adjustBrightness(10);
    createPost.adjustContrast(5);
    
    // Preview
    PreviewPage preview = createPost.preview();
    Assert.assertTrue(preview.isPostPreviewCorrect());
    
    // Publish
    FeedPage updatedFeed = preview.publish();
    
    // Verify post
    Assert.assertTrue(updatedFeed.hasPost("Just finished an amazing workout!"));
    Assert.assertTrue(updatedFeed.hasPostWithMedia());
    
    // Share post
    PostDetailPage postDetail = updatedFeed.openPost("Just finished an amazing workout!");
    SharePage share = postDetail.share();
    share.shareTo("WhatsApp", "friend3");
    
    // Verify share
    Assert.assertTrue(share.isSharedSuccessfully());
}
```

### Use Case 8: Live Streaming

```java
@Test(description = "Start and manage live stream")
public void testLiveStreaming() {
    HomePage homePage = new HomePage();
    
    // Start live stream
    LiveStreamPage liveStream = homePage.startLiveStream();
    liveStream.setTitle("Cooking Show - Making Pasta");
    liveStream.setCategory("Food & Cooking");
    liveStream.setPrivacy("Public");
    liveStream.addDescription("Join me as I make delicious pasta from scratch!");
    
    // Go live
    liveStream.goLive();
    Assert.assertTrue(liveStream.isLive());
    
    // Interact with viewers
    liveStream.readComments();
    liveStream.respondToComment("Great show!", "Thank you!");
    liveStream.showHeartReaction();
    
    // End stream
    liveStream.endStream();
    
    // Save recording
    StreamRecordingPage recording = liveStream.saveRecording();
    recording.setTitle("Cooking Show - Making Pasta");
    recording.setThumbnail("/path/to/thumbnail.jpg");
    recording.publish();
    
    // Verify saved
    VideosPage videos = homePage.viewVideos();
    Assert.assertTrue(videos.hasVideo("Cooking Show - Making Pasta"));
}
```

---

## Food Delivery Apps

### Use Case 9: Order Food Delivery

**Business Requirement**: Test complete food ordering and delivery tracking.

```java
@Test(description = "Order food and track delivery")
public void testFoodOrdering() {
    HomePage homePage = new HomePage();
    
    // Search restaurant
    RestaurantSearchPage search = homePage.searchRestaurant("Italian");
    search.filterByRating(4.0);
    search.filterByDeliveryTime(30);
    search.sortBy("Rating");
    
    // Select restaurant
    RestaurantPage restaurant = search.selectRestaurant("Mario's Italian");
    Assert.assertTrue(restaurant.hasGoodRating());
    Assert.assertTrue(restaurant.hasDeliveryAvailable());
    
    // Browse menu
    MenuPage menu = restaurant.viewMenu();
    menu.selectCategory("Pizza");
    
    // Add items to cart
    menu.addItem("Margherita Pizza", "Large", 1);
    menu.addItem("Caesar Salad", "Regular", 1);
    menu.addItem("Coca Cola", "500ml", 2);
    
    // View cart
    CartPage cart = menu.viewCart();
    Assert.assertEquals(cart.getItemCount(), 3);
    Assert.assertTrue(cart.getTotalAmount() > 0);
    
    // Apply coupon
    cart.applyCoupon("FIRSTORDER20");
    Assert.assertTrue(cart.isCouponApplied());
    
    // Checkout
    CheckoutPage checkout = cart.proceedToCheckout();
    checkout.enterDeliveryAddress("123 Main St, Apt 4B, New York, NY 10001");
    checkout.selectDeliveryTime("ASAP");
    checkout.selectPaymentMethod("Credit Card");
    checkout.enterCardDetails("4111111111111111", "12/25", "123");
    checkout.addDeliveryInstructions("Ring doorbell twice");
    
    // Place order
    OrderConfirmationPage confirmation = checkout.placeOrder();
    Assert.assertTrue(confirmation.isOrderPlaced());
    String orderId = confirmation.getOrderId();
    
    // Track order
    OrderTrackingPage tracking = homePage.trackOrder(orderId);
    Assert.assertEquals(tracking.getStatus(), "Preparing");
    
    // Wait for status updates
    tracking.waitForStatus("Out for Delivery", Duration.ofMinutes(15));
    Assert.assertEquals(tracking.getStatus(), "Out for Delivery");
    
    // Verify delivery
    tracking.waitForStatus("Delivered", Duration.ofMinutes(30));
    Assert.assertEquals(tracking.getStatus(), "Delivered");
    
    // Rate order
    RatingPage rating = tracking.rateOrder();
    rating.rateRestaurant(5);
    rating.rateFood(5);
    rating.rateDelivery(5);
    rating.addComment("Excellent service and food quality!");
    rating.submitRating();
}
```

---

## Travel & Booking Apps

### Use Case 10: Hotel Booking

**Business Requirement**: Test hotel search, booking, and payment.

```java
@Test(description = "Book hotel with flight")
public void testHotelBooking() {
    HomePage homePage = new HomePage();
    
    // Search hotels
    HotelSearchPage search = homePage.searchHotels();
    search.enterDestination("Paris, France");
    search.selectCheckInDate("2026-03-15");
    search.selectCheckOutDate("2026-03-20");
    search.selectGuests(2, 1); // 2 adults, 1 room
    search.search();
    
    // Filter results
    HotelListPage hotels = search.getResults();
    hotels.filterByPrice(100, 300);
    hotels.filterByRating(4);
    hotels.filterByAmenities("WiFi", "Pool", "Gym");
    hotels.sortBy("Price: Low to High");
    
    // Select hotel
    HotelDetailPage hotel = hotels.selectHotel("Grand Paris Hotel");
    Assert.assertTrue(hotel.hasAvailableRooms());
    Assert.assertTrue(hotel.hasGoodRating());
    
    // Select room
    RoomSelectionPage rooms = hotel.viewRooms();
    RoomDetailPage room = rooms.selectRoom("Deluxe Room");
    Assert.assertTrue(room.hasAmenities());
    Assert.assertTrue(room.hasPhotos());
    
    // Book room
    BookingPage booking = room.bookRoom();
    booking.enterGuestDetails(
        "John", "Doe", "john.doe@example.com", "+1234567890"
    );
    booking.addSpecialRequests("Late checkout if possible");
    booking.selectPaymentMethod("Credit Card");
    booking.enterCardDetails("4111111111111111", "12/25", "123", "John Doe");
    
    // Review booking
    ReviewPage review = booking.reviewBooking();
    Assert.assertEquals(review.getHotelName(), "Grand Paris Hotel");
    Assert.assertEquals(review.getCheckInDate(), "2026-03-15");
    Assert.assertEquals(review.getCheckOutDate(), "2026-03-20");
    
    // Confirm booking
    ConfirmationPage confirmation = review.confirmBooking();
    Assert.assertTrue(confirmation.isBookingConfirmed());
    String bookingId = confirmation.getBookingId();
    
    // Verify in bookings
    MyBookingsPage bookings = homePage.viewMyBookings();
    Assert.assertTrue(bookings.hasBooking(bookingId));
}
```

---

## Fitness & Wellness Apps

### Use Case 11: Workout Tracking

```java
@Test(description = "Track workout session")
public void testWorkoutTracking() {
    HomePage homePage = new HomePage();
    
    // Start workout
    WorkoutPage workout = homePage.startWorkout();
    workout.selectWorkoutType("Running");
    workout.setGoal("5 km", "30 minutes");
    
    // Start tracking
    workout.startTracking();
    Assert.assertTrue(workout.isTracking());
    
    // Simulate workout progress
    workout.updateDistance(1.0); // km
    workout.updatePace("6:00"); // min/km
    workout.updateHeartRate(150); // bpm
    workout.updateCalories(100);
    
    // Pause and resume
    workout.pause();
    Assert.assertFalse(workout.isTracking());
    workout.resume();
    Assert.assertTrue(workout.isTracking());
    
    // Complete workout
    workout.complete();
    
    // Review results
    WorkoutSummaryPage summary = workout.viewSummary();
    Assert.assertTrue(summary.getDistance() >= 1.0);
    Assert.assertTrue(summary.getDuration() > 0);
    Assert.assertTrue(summary.getCaloriesBurned() > 0);
    
    // Save workout
    summary.saveWorkout();
    summary.shareToSocialMedia("Facebook");
    
    // Verify in history
    WorkoutHistoryPage history = homePage.viewWorkoutHistory();
    Assert.assertTrue(history.hasWorkout("Running", "Today"));
}
```

---

## Enterprise Applications

### Use Case 12: Employee Time Tracking

```java
@Test(description = "Clock in and track time")
public void testTimeTracking() {
    LoginPage loginPage = new LoginPage();
    DashboardPage dashboard = loginPage.login("employee@company.com", "password");
    
    // Clock in
    TimeTrackingPage timeTracking = dashboard.openTimeTracking();
    timeTracking.clockIn();
    Assert.assertTrue(timeTracking.isClockedIn());
    String clockInTime = timeTracking.getClockInTime();
    
    // Add break
    timeTracking.startBreak("Lunch");
    Assert.assertTrue(timeTracking.isOnBreak());
    timeTracking.endBreak();
    Assert.assertFalse(timeTracking.isOnBreak());
    
    // Clock out
    timeTracking.clockOut();
    Assert.assertFalse(timeTracking.isClockedIn());
    
    // Verify timesheet
    TimesheetPage timesheet = dashboard.viewTimesheet();
    TimesheetEntry entry = timesheet.getTodayEntry();
    Assert.assertNotNull(entry.getClockInTime());
    Assert.assertNotNull(entry.getClockOutTime());
    Assert.assertTrue(entry.getTotalHours() > 0);
    
    // Submit timesheet
    timesheet.submitTimesheet();
    Assert.assertTrue(timesheet.isSubmitted());
}
```

---

## CI/CD Integration Scenarios

### Use Case 13: Nightly Regression Suite

**Scenario**: Run full regression suite every night on multiple devices.

**Jenkins Pipeline**:
```groovy
pipeline {
    agent any
    
    stages {
        stage('Run Regression Tests') {
            parallel {
                stage('Android Premium') {
                    steps {
                        sh 'mvn clean test -Ddevice.tier=premium -Dplatform=android'
                    }
                }
                stage('iOS Premium') {
                    steps {
                        sh 'mvn clean test -Ddevice.tier=premium -Dplatform=ios'
                    }
                }
                stage('Android Standard') {
                    steps {
                        sh 'mvn clean test -Ddevice.tier=standard -Dplatform=android'
                    }
                }
            }
        }
        
        stage('Generate Reports') {
            steps {
                publishHTML([
                    reportDir: 'reports/extent',
                    reportFiles: '**/*.html',
                    reportName: 'Test Reports'
                ])
            }
        }
    }
}
```

### Use Case 14: Pre-Commit Smoke Tests

**Scenario**: Run quick smoke tests before code merge.

```bash
# Run only smoke tests
mvn clean test -Dgroups=smoke -Dparallel.threads=2
```

### Use Case 15: Performance Testing

**Scenario**: Test app performance under load.

```java
@Test(description = "Performance test - Multiple concurrent users")
public void testConcurrentUsers() {
    // Simulate 10 concurrent users
    ExecutorService executor = Executors.newFixedThreadPool(10);
    List<Future<Boolean>> results = new ArrayList<>();
    
    for (int i = 0; i < 10; i++) {
        results.add(executor.submit(() -> {
            LoginPage loginPage = new LoginPage();
            HomePage homePage = loginPage.login("user@example.com", "password");
            return homePage.isPageLoaded();
        }));
    }
    
    // Verify all succeeded
    for (Future<Boolean> result : results) {
        Assert.assertTrue(result.get(), "All concurrent logins should succeed");
    }
}
```

---

## Summary

These real-world use cases demonstrate:

1. **Complex Workflows**: Multi-step user journeys
2. **Different App Types**: E-commerce, banking, healthcare, etc.
3. **Parallel Execution**: Running tests on multiple devices
4. **CI/CD Integration**: Automated testing in pipelines
5. **Performance Testing**: Load and stress testing

**Key Takeaways**:
- Framework handles complex scenarios
- Easy to extend for new use cases
- Supports both simple and complex workflows
- Works seamlessly with CI/CD pipelines

**Next Steps**:
1. Adapt these examples to your application
2. Create your own test scenarios
3. Integrate with your CI/CD pipeline
4. Monitor and improve test coverage
