   M S B A M L   `   `   `  ���� M  IFaceTrackingBasics, Version=0.0.0.0, Culture=neutral, PublicKeyToken=null7 clr-namespace:FaceTrackingBasicsFaceTrackingBasics  #    FaceTrackingBasics.MainWindow   W SPresentationCore, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35R NWindowsBase, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35\ XPresentationFramework, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35D 9http://schemas.microsoft.com/winfx/2006/xaml/presentation    R NSystem.Xaml, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089:x,http://schemas.microsoft.com/winfx/2006/xaml     2local clr-namespace:FaceTrackingBasics     -     � Title$  Face Tracking Basics��$	��640��$	��480��.��� -   $��MainGrid��$	��640��$	��480��.+��� -   $��
ColorImage��+   %FaceTrackingBasics.FaceTrackingViewer  -     Name$ faceTrackingViewer���� -   $��
rectangle1��$��23��$
��Left=�$��	7,371,0,0q�
���   �$	��Top=�$	��138��
��������� -   $��
rectangle2��$	��101��$
��Left=�$��	7,264,0,0q�
���   �$	��Top=�$	��139��
��������� -   $��button1��$��	DepthInit��$��23��$
��Left=�$��512,373,0,0q�$	��Top=�$��75���� -   $��textBlockDepth��$��23��$
��Left=�$��
11,373,0,0q�$	��Top=�$	��131������  �� ElementName$ slider1�� �� Path$ Value��� -	   $��slider1��$��23��$
��Left=�$��	7,402,0,0q�$	��Top=�$	��138�� � Value$
 0.70Q� � Maximum$ 1Q� � Minimum$	 0.6Q��� -
   $��checkBox_face��$��Send face info��$
��Left=�$��16,271,0,189q�$	��122���� -   $��checkBox_audio��$��Send audio info��$��20��$
��Left=�$��
16,0,0,141q�$��Bottom=�$	��122��{�. �� -   $��checkBox_movewindow��$��Move Window��$��20��$
��Left=�$��
16,296,0,0q�$	��Top=�$	��122�� d� 	IsChecked$
 TrueY��� -   $��checkBox_fixYdegree��$��fix Y degree��$��20��$
��Left=�{�. $��
16,0,0,115q�$��Bottom=�$	��122���� -   $��
labelDepth��$��Label��$��28��$
��Left=�$��530,412,0,0q�$	��Top=�