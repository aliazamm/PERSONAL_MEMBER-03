# PERSONAL MEMBER Android WebView

## GitHub-এ ব্যবহার
1. ZIP extract করুন।
2. সব ফাইল GitHub repository-তে upload করুন (root-এ `settings.gradle`, `build.gradle`, `app/`, `.github/` থাকবে)।
3. GitHub > Actions > Build PERSONAL MEMBER APK > Run workflow চাপুন।
4. Workflow শেষ হলে Artifacts থেকে `PERSONAL-MEMBER-APK` ডাউনলোড করুন।
5. ZIP extract করে `app-debug.apk` ফোনে ইনস্টল করুন।

## App URL
`app/src/main/java/com/personalmember/app/MainActivity.kt`-এর `homeUrl`-এ আপনার Apps Script URL রাখা হয়েছে।

## Permissions
শুধু INTERNET এবং ACCESS_NETWORK_STATE আছে। ফোন নম্বরের জন্য ACTION_DIAL ব্যবহৃত হয়; তাই CALL_PHONE permission লাগে না। ক্যামেরা/লোকেশন/মাইক্রোফোন স্বয়ংক্রিয়ভাবে চাওয়া হয়নি।

## Logo
আপনার দেওয়া লোগো `app/src/main/res/drawable-nodpi/app_logo.jpg` হিসেবে যুক্ত করা হয়েছে। এটি বর্তমানে project asset; পরবর্তী ধাপে launcher icon/splash-এ ব্যবহার করা যাবে।

## Note
Google sign-in কিছু ক্ষেত্রে Android WebView-তে Google-এর নিরাপত্তা নীতির কারণে বাধাপ্রাপ্ত হতে পারে। আপনার Apps Script ওয়েব অ্যাপ যদি Google login চায়, Chrome/Custom Tabs-এ authentication flow বা Apps Script deployment access settings যাচাই করতে হবে।
