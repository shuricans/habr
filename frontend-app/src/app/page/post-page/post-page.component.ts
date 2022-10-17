import { Component, OnInit } from '@angular/core';
import { MarkdownService } from 'ngx-markdown';

@Component({
  selector: 'app-post-page',
  templateUrl: './post-page.component.html',
  styleUrls: ['./post-page.component.scss']
})
export class PostPageComponent  {

  ngxMarkdownVersion = '14.0.1';

  markdown = `## Markdown Example Post.
---
<img src="https://www.cloudsavvyit.com/p/uploads/2022/03/f6ad612a.jpg" alt="drawing" width="100%"/>

---

### Syntax highlight
\`\`\`typescript
const language = 'typescript';
\`\`\`

### Lists
1. Ordered list
2. Another bullet point
   - Unordered list
   - Another unordered bullet

### Blockquote
> Blockquote to the max
# Markdown extensions

StackEdit extends the standard Markdown syntax by adding extra **Markdown extensions**, providing you with some nice features.

> **ProTip:** You can disable any **Markdown extension** in the **File properties** dialog.


## SmartyPants

SmartyPants converts ASCII punctuation characters into "smart" typographic punctuation HTML entities. For example:

|                |ASCII                          |HTML                         |
|----------------|-------------------------------|-----------------------------|
|Single backticks|\`'Isn't this fun?'\`            |'Isn't this fun?'            |
|Quotes          |\`"Isn't this fun?"\`            |"Isn't this fun?"            |
|Dashes          |\`-- is en-dash, --- is em-dash\`|-- is en-dash, --- is em-dash|`;
}
