Results are:
```
Draw test: 5000 objects all fit on screen
::      779 ms, Drawing with Path twice (Fill and Stroke)
::      210 ms, Drawing a pre-drawn Bitmap
::      170 ms, Drawing a pre-drawn Bitmap without Canvas transform
::      762 ms, Drawing same bitmap with BitmapShader[2px by 2px]
::      374 ms, Drawing same bitmap with BitmapShader[2px by 2px] without transforming bitmap to ALPHA_8 every time

Draw test: 5000 randomly over space of 3x3 screens (1 in 9 chance to )
::      130 ms, Drawing with Path twice (Fill and Stroke)
::      76 ms, Drawing a pre-drawn Bitmap
::      47 ms, Drawing a pre-drawn Bitmap without Canvas transform
::      441 ms, Drawing same bitmap with BitmapShader[2px by 2px]
::      98 ms, Drawing same bitmap with BitmapShader[2px by 2px] without transforming bitmap to ALPHA_8 every time

Canvas size: [720, 1280]
```