import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;


public class FileBrowser extends JPanel implements ComponentListener {
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private JPanel affichagePanel, showPanel;
	private JButton renameFileButton, addFileButton, addFolderButton, retButton, saveFileButton;
	private JLabel footerInfoLabel;
	private DefaultMutableTreeNode computer, root;
	private DefaultTreeModel treeModel;
	private JTree tree;
	private JTextField arbreTextField;
	private JScrollPane treeScroll, showScrollPane;
	private String currentFolder = "", selectedFolder = null;
	private int width = 1000;
	private int height = 700;
	private FileSystemView fileSystemView;
	private JTextArea textArea;

	private void build() {
//		width = 950;
//		height = 600;
//		try {
//			// Significantly improves the look of the output in
//			// terms of the file names returned by FileSystemView!
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (Exception e) {
//		}
		frame = new JFrame("File Browser");
		frame.setPreferredSize(new Dimension(width, height));
		frame.setSize(frame.getPreferredSize());
		frame.setLocationRelativeTo(null);
		frame.pack();
//		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setFocusable(true);
		frame.setBackground(Color.black);
		frame.setIconImage(getImg("img/file4.jpg"));
		frame.add(this, BorderLayout.CENTER);

		setPreferredSize(new Dimension(width, height));
		setSize(getPreferredSize());
		setBackground(Color.BLACK);
		setFocusable(true);
		setBorder(BorderFactory.createBevelBorder(0, Color.black, Color.black));
		setLayout(new BorderLayout(0, 0));

		retButton = new JButton(" ");
		retButton.setIcon(new ImageIcon(getImg("img/back.png", 40, 30).getImage()));
		retButton.setBackground(new Color(0, 0, 0, 0));
		retButton.setOpaque(false);
		retButton.setFocusable(false);

		arbreTextField = new JTextField("=> Computer");
		arbreTextField.setEditable(false);
		arbreTextField.setOpaque(false);
		arbreTextField.setForeground(Color.white);
		arbreTextField.setFont(new Font("Tahoma", Font.BOLD, 14));

		showPanel = new JPanel();
		showPanel.setOpaque(false);
		showPanel.setLayout(new FlowLayout(0));

		addFolderButton = new JButton("new Folder");
		addFolderButton.setOpaque(false);
		addFolderButton.setBackground(new Color(0, 0, 0, 0));
		addFolderButton.setForeground(Color.WHITE);
		addFolderButton.setFocusable(false);

		addFileButton = new JButton("new File");
		addFileButton.setOpaque(false);
		addFileButton.setBackground(new Color(0, 0, 0, 0));
		addFileButton.setForeground(Color.WHITE);
		addFileButton.setFocusable(false);

		renameFileButton = new JButton("Rename");
		renameFileButton.setOpaque(false);
		renameFileButton.setBackground(new Color(0, 0, 0, 0));
		renameFileButton.setForeground(Color.WHITE);
		renameFileButton.setFocusable(false);

		saveFileButton = new JButton("Save");
		saveFileButton.setOpaque(false);
		saveFileButton.setBackground(new Color(0, 0, 0, 0));
		saveFileButton.setForeground(Color.WHITE);
		saveFileButton.setFocusable(false);

		JPanel bttonsFooterPanel = new JPanel();
		bttonsFooterPanel.setOpaque(false);
		bttonsFooterPanel.setLayout(new FlowLayout(0));
		bttonsFooterPanel.add(saveFileButton);
		bttonsFooterPanel.add(renameFileButton);
		bttonsFooterPanel.add(addFileButton);
		bttonsFooterPanel.add(addFolderButton);

		File file = new File(currentFolder);
		footerInfoLabel = new JLabel();
		footerInfoLabel.setIcon(fileSystemView.getSystemIcon(file));
		footerInfoLabel.setText(" 2 element(s)");
		footerInfoLabel.setToolTipText(file.getPath());
		footerInfoLabel.setPreferredSize(new Dimension(120, 60));
		footerInfoLabel.setSize(footerInfoLabel.getPreferredSize());
		footerInfoLabel.setForeground(Color.white);

		JPanel footerPanel = new JPanel();
		footerPanel.setPreferredSize(new Dimension(750, getHeight() / 20));
		footerPanel.setSize(footerPanel.getPreferredSize());
		footerPanel.setBackground(Color.black);
		footerPanel.setLayout(new BorderLayout(0, 0));
		footerPanel.add(footerInfoLabel, BorderLayout.WEST);
		footerPanel.add(bttonsFooterPanel, BorderLayout.EAST);

		JPanel barPanel = new JPanel();
		barPanel.setOpaque(false);
		barPanel.setLayout(new BorderLayout(0, 0));
		barPanel.add(retButton, BorderLayout.WEST);
		barPanel.add(arbreTextField, BorderLayout.CENTER);

		affichagePanel = new JPanel();
		affichagePanel.setPreferredSize(new Dimension(getWidth() * 7 / 10, getHeight()));
		affichagePanel.setOpaque(false);
		affichagePanel.setBorder(BorderFactory.createBevelBorder(0, Color.black, Color.black));
		affichagePanel.setLayout(new BorderLayout(0, 0));
		affichagePanel.add(barPanel, BorderLayout.NORTH);
		affichagePanel.add(footerPanel, BorderLayout.SOUTH);
	}

	@SuppressWarnings("deprecation")
	public FileBrowser() {
		// TODO Bismi allah ^_^
		fileSystemView = FileSystemView.getFileSystemView();
		build();

		computer = new DefaultMutableTreeNode("");
		DefaultMutableTreeNode node1 = null;
		File[] files = File.listRoots();
		for (File file : files) {
			node1 = new DefaultMutableTreeNode(file);
			computer.add(node1);
			node1.add(new DefaultMutableTreeNode(new Boolean(true)));
			remplirShowPane(file, 0);
		}

//		File fileRoot = fileSystemView.getHomeDirectory();// new File("C:\\Users\\The Mh\\Desktop\\");

//		File[] roots = fileSystemView.getRoots();

//		root = new DefaultMutableTreeNode(fileRoot);

		treeModel = new DefaultTreeModel(computer);
//		treeModel = new DefaultTreeModel(root);
//		createChildren(root);
		tree = new JTree(treeModel);
		tree.setOpaque(false);
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		tree.setCellRenderer(new FileTreeCellRenderer());
		tree.addMouseListener(new Mouse());
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			// TODO for Detect iny File or Directory selected in JTree
			@SuppressWarnings("unused")
			@Override
			public void valueChanged(TreeSelectionEvent event) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
				File file = new File(node.getUserObject().toString());
				if (file != null) {
					currentFolder = file.getAbsolutePath();
					arbreTextField.setText(currentFolder);
				} else {
					arbreTextField.setText("Computer");
					currentFolder = "";
				}
				OpenFile(file);
				selectedFolder = null;
			}
		});

		tree.addTreeExpansionListener(new TreeExpansionListener() {
			// Make sure expansion is threaded and updating the tree model
			// only occurs within the event dispatching thread.
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				final DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

				Thread runner = new Thread() {
					public void run() {
						node.removeAllChildren();
						createChildren(node);
						Runnable runnable = new Runnable() {
							public void run() {
								treeModel.reload(node);
							}
						};
						SwingUtilities.invokeLater(runnable);
					}
				};
				runner.start();
			}

			public void treeCollapsed(TreeExpansionEvent event) {
			}
		});
		tree.expandRow(1);

		///// >>>>>>>>>>>>>>>>>>
		treeScroll = new JScrollPane(tree);
		treeScroll.setPreferredSize(new Dimension(250, (int) getHeight()));
		treeScroll.setOpaque(false);
		treeScroll.setBorder(BorderFactory.createBevelBorder(0, Color.black, Color.black));
		treeScroll.getViewport().setOpaque(false);

		retButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (currentFolder != null && !currentFolder.equals("")) {
					currentFolder = new File(currentFolder).getParent();
					if (currentFolder != null && !currentFolder.equals("")) {
						arbreTextField.setText(currentFolder);
						OpenFile(new File(currentFolder));
					}
				}
				selectedFolder = null;
			}
		});

		addFolderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String nameFolder = JOptionPane.showInputDialog(frame, " Enter the Name of new Folder : ");
				if (nameFolder != null && !nameFolder.equals("")) {
					new File(currentFolder + "\\" + nameFolder).mkdirs();
					OpenFile(new File(currentFolder));
				}
				selectedFolder = null;
			}
		});

		addFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String nameFile = JOptionPane.showInputDialog(frame, " Enter the Name of new File.txt : ");
				if (nameFile != null && !nameFile.equals("")) {
					if (selectedFolder == null) {

						currentFolder = currentFolder + "\\" + nameFile + ".txt";
						File f = new File(currentFolder);
						writeFile(f.toPath(), "");
						OpenFile(f);
					} else {
						selectedFolder = selectedFolder + "\\" + nameFile + ".txt";
						currentFolder = selectedFolder;
						File f = new File(selectedFolder);
						writeFile(f.toPath(), "");
						OpenFile(f);
					}
				}
				selectedFolder = null;
			}
		});

		renameFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (selectedFolder != null) {
					String nameFile = JOptionPane.showInputDialog(frame, " Enter the new Name : ");
					if (nameFile != null && !nameFile.equals("")) {
						File file = new File(selectedFolder);
						selectedFolder = file.getParent() + "\\" + nameFile;
						if (file.isDirectory())
							file.renameTo(new File(selectedFolder));
						else
							file.renameTo(new File(selectedFolder + ".txt"));
						OpenFile(new File(currentFolder));
						arbreTextField.setText(currentFolder);
					}
					selectedFolder = null;
				}
			}
		});

		saveFileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				File f = new File(currentFolder);
				if (f.exists())
					try {
						String fileType = Files.probeContentType(f.toPath());
						if (fileType != null)
							if (fileType.equals("text/plain"))
								writeFile(f.toPath(), textArea.getText());
					} catch (IOException e) {
						System.out.println("!! Ereur : " + e);
					}
			}
		});

		JScrollPane AfficageScroll = new JScrollPane(showPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		AfficageScroll.setOpaque(false);
		AfficageScroll.getViewport().add(showPanel);
		AfficageScroll.getViewport().setOpaque(false);
		AfficageScroll.getViewport().validate();
		AfficageScroll.setPreferredSize(new Dimension(750, (int) AfficageScroll.getPreferredSize().getHeight()));
		affichagePanel.add(AfficageScroll, BorderLayout.CENTER);
//		affichagePanel.add(showPanel, BorderLayout.CENTER);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, affichagePanel);
		splitPane.setOpaque(false);
		add(splitPane, BorderLayout.CENTER);
		validate();
		// --------------------------------------------------------------------------------

		frame.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE)
					retButton.doClick();
				else if ((event.getKeyCode() == KeyEvent.VK_N) && ((event.getModifiers() & KeyEvent.CTRL_MASK) != 0))
					addFolderButton.doClick();
				else if ((event.getKeyCode() == KeyEvent.VK_R) && ((event.getModifiers() & KeyEvent.CTRL_MASK) != 0))
					renameFileButton.doClick();
			}
		});
		frame.validate();
		frame.setVisible(true);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);// clear and repaint
		g.drawImage(getImg("img/background7.jpg"), 0, 0, getWidth(), getHeight(), this);
		g.drawImage(getImg("img/Hacker-silhouette.jpg"), (int) treeScroll.getWidth(), 0,
				(int) getWidth() - treeScroll.getWidth(), getHeight(), this);
	}

	// TODO OpenFile for open selected file selected from JTree and showit in Show
	// is Panel
	public void OpenFile(File file) {
		if (file.exists()) {
			showPanel.removeAll();
			showPanel.setLayout(new FlowLayout(0));
			if (!file.isDirectory()) {
				footerInfoLabel.setText(" File zise : " + file.length() + " bytes");
				footerInfoLabel.setIcon(fileSystemView.getSystemIcon(file));
				String fileType = "Undetermined";
				try {
					fileType = Files.probeContentType(file.toPath());
				} catch (IOException ioException) {
					fileType = "Undetermined";
					System.out.println("!! ERROR: " + ioException);
				}
				if (fileType == null)
					fileType = "Undetermined";

				if (fileType.toString().equals("text/plain")) {// || fileType.toString().equals("application/pdf")) {
					if (fileType.toString().equals("text/plain"))
						// TEXTE
						textArea = new JTextArea(readFile(file.toString()));
					else
						textArea = new JTextArea(readPdf(currentFolder));
					textArea.setBackground(Color.LIGHT_GRAY);
					textArea.setForeground(Color.black);
					textArea.setFont(new Font("Tahoma", Font.BOLD, 13));

					showScrollPane = new JScrollPane();
					showScrollPane.getViewport().add(textArea, BorderLayout.CENTER);
					showScrollPane.setPreferredSize(showPanel.getSize());
					showScrollPane.validate();
					showPanel.add(showScrollPane, BorderLayout.CENTER);
					showPanel.validate();

//				} else if (fileType.toString().equals("application/pdf")) {

//			} else if (fileType.toString().equals("video/mp4") || fileType.toString().equals("video/webm")) {
//
//			} else if (fileType.toString().equals("audio/mpeg")) {
//
//			} else if (fileType.toString().equals("application/x-zip-compressed")) {

				} else {
					Image img;
					if (fileType.toString().equals("image/jpeg") || fileType.toString().equals("image/png")
							|| fileType.toString().equals("image/jpg") || fileType.equals("image/gif")) {

						img = new ImageIcon(file.toString()).getImage();

					} else {
//						System.out.println(fileType);
						img = getImg("img/unsupported2.png");
						if (Desktop.isDesktopSupported()) {
							try {
								Desktop.getDesktop().open(file);
							} catch (IOException ex) {
								System.err.println("!! Ereur : " + ex);
							}
						}
					}
					img = img.getScaledInstance(showPanel.getWidth(), showPanel.getHeight(), Image.SCALE_SMOOTH);

					JLabel label = new JLabel(new ImageIcon(img));
					label.setPreferredSize(showPanel.getSize());
					showPanel.add(label, BorderLayout.CENTER);
				}

			} else {
				showPanel.setLayout(new GridLayout(0, 6, 10, 10));
				File[] files = file.listFiles();
				if (files != null) {
					footerInfoLabel.setText(" " + files.length + " element(s)");
					footerInfoLabel.setIcon(fileSystemView.getSystemIcon(file));
					for (File f : files) {
						remplirShowPane(f, 1);
					}
				}
			}
			revalidate();
			repaint();
		}
	}

	private void remplirShowPane(File f, int chois) {
		JButton fileButton = new JButton();
		try {
			ImageIcon imgIcon = (ImageIcon) fileSystemView.getSystemIcon(f);
			Image img = imgIcon.getImage();
			img = img.getScaledInstance(25, 35, Image.SCALE_SMOOTH);
			fileButton.setIcon(new ImageIcon(img));
			fileButton.setText(fileSystemView.getSystemDisplayName(f));
			fileButton.setHorizontalTextPosition(SwingConstants.CENTER);
			fileButton.setVerticalTextPosition(JButton.BOTTOM);
			fileButton.setToolTipText(f.getPath());
			if (chois == 0)
				fileButton.setPreferredSize(new Dimension(140, 150));
			else
				fileButton.setPreferredSize(new Dimension(100, 120));
			fileButton.setSize(fileButton.getPreferredSize());
			fileButton.setBackground(new Color(0, 0, 0, 0));
			fileButton.setForeground(Color.white);
			fileButton.setOpaque(false);
			fileButton.addMouseListener(new Mouse(fileButton.getToolTipText()));
			showPanel.add(fileButton);
		} catch (Exception e) {
			System.out.println("!! Ereur : " + e);
//			e.getStackTrace();
		}
	}

	private String readFile(String file) {
		// LIS LE FICHIER
		String lines = "";
		String line;

		try {
			// CREE LE FLUX
			BufferedReader reader = new BufferedReader(new FileReader(file));
			// LIS LIGNE A LIGNE
			while ((line = reader.readLine()) != null)
				lines += line + "\n";
			reader.close();
		} catch (Exception e) {
			lines = "Une erreur s'est produite durant la lecture du flux : " + e.getMessage();
		}
		return lines;
	}

	private void writeFile(Path pt, String contenu) {
		try (BufferedWriter bw = Files.newBufferedWriter(pt, Charset.forName("UTF-8"), StandardOpenOption.CREATE)) {
			bw.write(contenu);
		} catch (Exception e) {
			System.out.println("!! Ereur in function writeFile(Path pt, String contenu) :\n=>  " + e.getMessage());
		}
	}

	private String readPdf(String pdfFile) {
//		try {
//			PdfReader reader = new PdfReader(pdfFile);
//			StringBuffer sb = new StringBuffer();
//			PdfReaderContentParser parser = new PdfReaderContentParser(reader);
//			TextExtractionStrategy strategy;
//			for (int cmp = 1; cmp <= reader.getNumberOfPages(); cmp++) {
//				strategy = parser.processContent(cmp, new SimpleTextExtractionStrategy());
//				sb.append(strategy.getResultantText());
//			}
//			reader.close();
//			return sb.toString();
//		} catch (IOException e) {
//			throw new IllegalArgumentException("Not able to read file " + pdfFile, e);
//		}
		return "";
	}

	@SuppressWarnings({ "deprecation" })
	private void createChildren(DefaultMutableTreeNode node) {
		File fileRoot = new File(node.getUserObject().toString());
		File[] files = fileRoot.listFiles();
		if (files != null) {
			for (File file : files) {
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file);
				node.add(childNode);
				if (file.isDirectory())
					childNode.add(new DefaultMutableTreeNode(new Boolean(true)));
//					createChildren(childNode);
			}
		} else
			node.add(new DefaultMutableTreeNode(new Boolean(true)));
	}

	public Image getImg(String sh) {
		try {
//			return new ImageIcon(getClass().getResource(sh)).getImage();
			return new ImageIcon("src\\"+sh).getImage();
		} catch (Exception e) {
			System.out.println("!! Ereur : image {\"" + sh + "\"} not found :: " + e.getMessage());
		}
		return null;
	}

	public ImageIcon getImg(String sh, int width, int height) {
		try {
			Image img = getImg(sh);
			img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);// getImg(sh).getScaledInstance(width,
																			// height, Image.SCALE_SMOOTH)
			return new ImageIcon(img);
		} catch (Exception e) {
			System.out.println("!! Ereur : image {\"" + sh + "\"} not found :: " + e.getMessage());
			return null;
		}
	}

	private void showMessageDialog(String sh) {
		JOptionPane.showMessageDialog(this, sh, "Not Enabled", JOptionPane.ERROR_MESSAGE);
	}

	public class Mouse extends MouseAdapter {
		private String sh;

		public Mouse(String sh) {
			this.sh = sh;
		}

		public Mouse() {
			super();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger())
				doPop(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
//			super.mousePressed(e);
			if (e.isPopupTrigger())
				doPop(e);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() != tree) {
				if (e.getClickCount() == 1) {
					selectedFolder = sh;
				} else {
					currentFolder = sh;
					OpenFile(new File(currentFolder));
					arbreTextField.setText(currentFolder);
					selectedFolder = null;
				}
			}
		}

		private void doPop(MouseEvent e) {
			PopUpDemo menu = new PopUpDemo(e);
			menu.show(e.getComponent(), e.getX(), e.getY());
		}

		// TODO PopMrnu
		class PopUpDemo extends JPopupMenu {
			private static final long serialVersionUID = 1L;
			JMenuItem rename, addFolder, addFile, openInDesktop;

			public PopUpDemo(MouseEvent e) {
				rename = new JMenuItem("-> Rename ");
				rename.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent event) {
//						selectedFolder=e.getComponent().getParent().getName();
//						System.out.println(selectedFolder);
						renameFileButton.doClick();
					}
				});
				addFolder = new JMenuItem("-> new Folder ");
				addFolder.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						addFolderButton.doClick();
					}
				});
				addFile = new JMenuItem("-> new File ");
				addFile.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						addFileButton.doClick();
					}
				});
				openInDesktop = new JMenuItem("-> Open in Desktop ");
				openInDesktop.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						String sh = "";
						if (selectedFolder == null || selectedFolder.equals(""))
							sh = currentFolder;
						else
							sh = selectedFolder;
						if (sh != null && !sh.equals("")) {
							File file = new File(sh);
							if (Desktop.isDesktopSupported()) {
								try {
									Desktop.getDesktop().open(file);
								} catch (IOException ex) {
									System.err.println("!! Ereur : " + ex);
								}
							}
						} else
							showMessageDialog("You chode select File");
					}
				});
				add(rename);
				add(addFolder);
				add(addFile);
				add(openInDesktop);
			}
		}
	}

	/** A TreeCellRenderer for a File. */
	class FileTreeCellRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 1L;

		private JLabel label;

		FileTreeCellRenderer() {
			label = new JLabel();
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			File file = new File(node.getUserObject().toString());
			label.setIcon(fileSystemView.getSystemIcon(file));
			label.setText(fileSystemView.getSystemDisplayName(file));
			label.setToolTipText(file.getPath());
			label.setOpaque(false);
			return label;
		}
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {

	}

	@Override
	public void componentMoved(ComponentEvent arg0) {

	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		// TODO wath cmp can dow if the taille of JFrame has changed
		width = getWidth();
		height = getHeight();
		revalidate();
		repaint();
	}

	@Override
	public void componentShown(ComponentEvent arg0) {

	}

	// --------------------- test
	// ------------------------------------------------------------------
	public static void main(String[] args) {
		new FileBrowser();
	}
}
