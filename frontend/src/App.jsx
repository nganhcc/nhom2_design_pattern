import { Route, Routes } from 'react-router-dom';
import { DemoProvider } from './context/DemoContext.jsx';
import Layout from './components/Layout.jsx';
import HomePage from './pages/HomePage.jsx';
import SearchPage from './pages/SearchPage.jsx';
import VideoPage from './pages/VideoPage.jsx';
import ChannelPage from './pages/ChannelPage.jsx';
import UploadPage from './pages/UploadPage.jsx';
import NotFoundPage from './pages/NotFoundPage.jsx';

export default function App() {
  return (
    <DemoProvider>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<HomePage />} />
          <Route path="/search" element={<SearchPage />} />
          <Route path="/videos/:videoId" element={<VideoPage />} />
          <Route path="/channels/:channelId" element={<ChannelPage />} />
          <Route path="/upload" element={<UploadPage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Routes>
    </DemoProvider>
  );
}
