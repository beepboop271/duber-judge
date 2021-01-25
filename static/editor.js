var MathJax = {
  startup: { typeset: false },
  loader: { load: ["mathjax-asciimath-3.1.2.js"] },
  asciimath: { delimiters: [["@@", "@@"]] },
};

let input = document.getElementById("input");
let stoppedTypingTimer;

let output = document.getElementById("output");
output.innerHTML = marked(input.value);

function render() {
  MathJax.typesetClear()
  output.innerHTML = DOMPurify.sanitize(marked(input.value));
  MathJax.typeset();
}

// runs after mathjax loads
window.onload = render;

input.addEventListener('keydown', function() {
  clearTimeout(stoppedTypingTimer);
  stoppedTypingTimer = setTimeout(render, 1000);
});
