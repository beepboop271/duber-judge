var MathJax = {
  startup: { typeset: false },
  loader: { load: ["mathjax-asciimath-3.1.2.js"] },
  asciimath: { delimiters: [["@@", "@@"]] },
};


const input = $('#problem-viewing-latex-input')[0];
console.log(input);
const out = $('#problem-viewing-latex')[0];
out.innerHTML = marked(input.value);

window.onload = function() {
  MathJax.typeset();
};