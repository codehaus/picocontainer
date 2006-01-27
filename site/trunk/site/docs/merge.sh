#!/bin/sh

rm two.pdf
rm PicoContainerDocumentation-1.2.pdf
pdfmeld -pages 3-300 downloaded.pdf two.pdf
pdfmeld  front-page.pdf,two.pdf PicoContainerDocumentation-1.2.pdf
