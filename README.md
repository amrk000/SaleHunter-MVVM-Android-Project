<div align="center">
<img src="https://user-images.githubusercontent.com/63168118/200122470-6da06ee5-47c8-42f4-81c3-cd5a5985ca4d.png" />
</div>

<h2>About Project:</h2>
<h3>Sale Hunter is a product search engine that provides a platform for both customer and seller. It helps customers hunt the best deals on any product they need and find the nearest store that selling it easily. They can aslo search for all products available on online e-commerce websites in one place quickly. On the other hand,  seller can create a store page and showcase his products totally for free to reach more customers online with less efforts.</h3>
        
</br>

<div align="center">
<table>
<tr>
<td width="auto">
        
<h2>App Features:</h2>
<ul>
<li>Dark and light theme.</li>
<li>Support multi languages (English/Arabic).</li>
<li>Sign in & up using email, Facebook and google authentication.</li>
<li>Searching for products using text keywords, voice recognition or
product barcode scanning using camera.</li>
<li>Using GPS to detect user location and help him find nearest store that
selling the needed product.</li>
<li>Ability to see and control map containing all stores that selling the
needed product with button for navigation mode activation in google
maps.</li>
<li>Providing all info about product including images, price, description,
price tracker, store location, and user rate action.</li>
<li>Access to on sale products, favorite products and user history.</li>
<li>Seller User can create, edit or delete his own store.</li>
<li>Seller User can access store dashboard to manage his products.</li>
<li>Access to profile info and ability to edit user data.</li>
</ul>
        <br>
      <div align="center">
  <a href="https://dl.dropbox.com/s/svb74wt5qb60z6g/SaleHunter%20v0.9.6%20Pre-Alpha.apk">
    <img width="200px" src="https://user-images.githubusercontent.com/63168118/157979139-36dbcfe4-c82d-43b9-85d3-0e45eeba05d9.png"/>
  </a>
  <p>⚠ Backend server is out of service</p>
  </div>    
<td>

<td width="auto">
<div align="center"><video src="https://user-images.githubusercontent.com/63168118/200121706-cf2e9ab1-2084-409d-a71c-8c7c4c3bb2bc.mp4"></div>
</td>
        
</tr>
</table>
</div>

<hr>

<h2>Implementation Highlights:</h2>
<ul>
  <li>Android MVVM Architectural Pattern with livedata & RxJava</li>
  <li>Rest Api Data Handling using Gson, Retrofit & Glide</li>
  <li>Search using text, Voice Recognition & Barcode</li>
  <li>Multi-Views RecyclerView Adapter</li>
  <li>Nested RecyclerViews (horizontal scroll inside vertical list)</li>
  <li>Infinite scroll data loading (Lazy Load) without 3rd party library</li>
  <li>Nested Fragments Navigation with Navigation Component</li>
  <li>ViewPager v1 & v2 implementation: App Intro, Vertical Feature Intro & Products Images Slideshow</li>
  <li>Image Base64 encoder implementation</li>
  <li>Text Fields Validation using REGEX: Full Name, user Name ID, Email, Password & Website</li>
  <li>Side Menu Custom implementation without 3rd party library</li>
  <li>Navbar Custom implementation without 3rd party library</li>
  <li>Loading pre-made SQL Lite databse filde using Room Persistence</li>
  <li>Multi languages support LTR/RTL with custom font for each language</li>
  <li>Facebook & Google Sign in Auth</li>
  <li>Charts Implementation for Price Tracking</li>
  <li>Google Maps Implemntation with GPS real-time tracking</li>
</ul>

<hr>

<h2>Important Notes:</h2>
<ul>
  <li>SaleHunter Project is a Pre-Alpha not completed version that contains bugs</li>
  <li>The backend server is down so many features won't work properly</li>
  <li>The project aims to be a good reference for features implementations to help developers and not to be used by normal users</li>
  <li>Arabic Language is auto translated by google translation service</li>
</ul>

<hr>

<h2>Copyrights:</h2>
<ul>
  <li>SaleHunter Logo: My own design</li>
  <li>UI Design:  <a href="https://www.behance.net/gallery/149958971/Sale-Hunter-%28-Price-Search-Engine-%29-UX-Case-Study">By Nadine Essam</a></li>
</ul>

<hr>
<h2>Project Changelog: </h2>
  <ol>
  
  <li><b>v0.1.0 Pre-Alpha</b></li>
  Project Initialization
  
  <hr>
  <li><b>v0.2.1 Pre-Alpha</b></li>
    <ul>
      <li>Illustrations Updated</li>
      <li>Transitions are improved</li>
      <li>Social media auth are now hidden if gms aren't available</li>
      <li>Fixed back button visual bug</li>
    </ul>
  
  <hr>
  <li><b>v0.3.1 Pre-Alpha</b></li>
    <ul>
      <li>Side Menu Implementation</li>
      <li>Home Implementation:</li>
      <ul>
       <li>Search Bar</li>
       <li>Voice Search</li>
       <li>Barcode Search</li>
       <li>Recommended Products</li>
       <li>Bottom Navbar</li>
      </ul>
    </ul>

  <hr>
  <li><b>v0.4.2 Pre-Alpha</b></li>
    <ul>
      <li>Performance improved & animation lag fixed</li>
      <li>network disconnected screen changed to skippable dialog</li>
      <li>new loading screen design</li>
      <li>navbar width adjustment for smaller screens</li>
      <li>side menu size improved for smaller screens</li>
      <li>social media signout bug fixed</li>
      <li>added vibration & sound effect to scanner on capture</li>
      <li>product card view layout improved</li>
      <li>Verification pin increased from 4 to 6 digits</li>
      <li>RestApi Implementation:</li>
      <ul>
       <li>Sign In</li>
       <li>Sign Up</li>
       <li>Social Media Auth</li>
       <li>Password Reset</li>
      </ul>
  </ul>
  
  <hr>
  <li><b>v0.5.3 Pre-Alpha</b></li>
    <ul>
      <li>UI/UX Improvements:</li>
      <ul>
       <li>Dialogs New Design</li>
       <li>Forced signout better UX</li>
       <li>Side Menu Design Improved</li>
       <li>Menu username & profile picture is now active</li>
      </ul>
      <li>User profile implementation:</li>
      <ul>
       <li>load user data</li>
       <li>change picture</li>
       <li>change username</li>
       <li>change email with verification flow</li>
       <li>change password</li>
      </ul>
      <li>Backend REST API updates</li>
      <li>App DataModels updated</li>
      <li>Image Encoder Implemented: handles image compression, resize 500x500, conversion to WEBP & Base64 Encode</li>
      <li>Code Refactoring:</li>
      <ul>
       <li>API Response Codes Single Provider</li>
       <li>Dialogs Single Provider</li>
       <li>Global Account Manager</li>
       <li>Global Text Field Validator</li>
      </ul>
  </ul>
  
  <hr>
  <li><b>v0.6.4 Pre-Alpha</b></li>
    <ul>
      <li>Code Refactor: Fully Converted to MVVM</li>
      <li>Search Functionality & filtering implementation</li>
      <li>Search Results Page & Stores Map implementation</li>
      <li>Product Page implementation</li>
      <li>Recommended Products implementation</li>
      <li>Social Media update in Sign In/up & User Profile</li>
      <li>Navbar & Menu options Changed</li>
      <li>Product Card/List Design Adjustments</li>
      <li>Favorites List implementation</li>
      <li>User History implementation</li>
      <li>Barcode Scanner Product Detection implementation</li>
      <li>Bugs Fixed:</li>
      <ul>
       <li>user data doesn't load on sign in</li>
       <li>Sign out crash</li>
       <li>Sign Up Error 409 instead of user exists message</li>
       <li>Unchecked Remember Me in Sign in</li>
       <li>On Activity Resume Dialog Bug Fixed</li>
       <li>Forced Signout Message Dialog</li>
       <li>Profile Save Button Still visible when image changed & saved</li>
       <li>fragment killed on backstack</li>
      </ul>
  </ul>
  
  <hr>
  <li><b>v0.7.5 Pre-Alpha</b></li>
    <ul>
      <li>Barcode: 4000+ Egyptian Products Support added to Total of 406+ Million Global Product</li>
      <li>Account Type in Menu & Profile</li>
      <li>Load Online Stores Logo From Server instead of loading locally</li>
      <li>Rendering Stores location Marks on map in results & product page</li>
      <li>Store page implementation</li>
      <li>Create Store implementation</li>
      <li>On Sale Page implementation</li>
      <li>Product Page:</li>
      <ul>
       <li>Product Rating implementation</li>
       <li>Navigation button implementation for local stores</li>
       <li>back button implementation</li>
       <li>Sale Percent implementation</li>
       <li>Product Multi Images Slider implementation</li>
       <li>Product Description Implementation</li>
       <li>Share Button changed to share SaleHunter's product page instead of source website URL</li>
      </ul>
      <li>Bugs Fixed:</li>
      <ul>
       <li>Fixed Visual bug with long category in product card</li>
       <li>Favourite List Del Icon Scale Bug</li>
       <li>Searchbar text not showing in light mode</li>
      </ul>
      <li>API Response Errors handeled:</li>
      <ul>
       <li>Cloudinary link returns http:// instead of https:// Causes image load fail</li>
       <li>Rate returns null instead of 0</li>
       <li>Convert product_sale from discounted price to percent price</li>
      </ul>
  </ul>

   <hr>
  <li><b>v0.8.6 Pre-Alpha</b></li>
    <ul>
      <li>GPS user location auto refresh changed to be every 10 meters or 1 minute interval</li>
      <li>Sale Hunter Shared links detection in Open With Menu</li>
      <li>About implementation (Based on SaleHunter Website)</li>
      <li>Seller Dashboard Implementation</li>
      <li>Bugs Fixed:</li>
      <ul>
       <li>Sort by nearest store fixed</li>
       <li>Back stack navigation bug fixed</li>
       <li>Product rate long precision fixed</li>
       <li>Discounted prices bug fixed</li>
      </ul>
  </ul>
  
  <hr>
  <li><b>v0.9.6 Pre-Alpha</b></li>
    <ul>
      <li>Settings Page Implementation</li>
      <li>Multi Language Implementation Arabic/English</li>
      <li>Different Font Family for each language</li>
  </ul>

  </ol>
</br>
