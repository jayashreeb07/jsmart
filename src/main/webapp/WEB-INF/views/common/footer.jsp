<button id="chatBtn" title="Chat with us">💬</button>
<div id="chatPanel" hidden>
  <div id="chatHead">JS Mart Assistant <span id="chatClose">✕</span></div>
  <div id="chatBody"></div>
  <div id="chatInput"><input id="chatText" maxlength="500" placeholder="Ask about orders, returns..."/><button id="chatSend">Send</button></div>
</div>
<script src="${pageContext.request.contextPath}/assets/js/chat.js"></script>
<footer class="foot"><p>JS Mart · Servlet/JSP capstone · <a href="${pageContext.request.contextPath}/api/v1/health">health</a></p></footer>
